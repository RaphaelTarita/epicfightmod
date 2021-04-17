package maninhouse.epicfight.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;
import com.google.common.collect.Lists;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.BowCapability;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HandProperty;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WieldStyle;
import maninhouse.epicfight.capabilities.item.CrossbowCapability;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CapabilityConfig {
	public static final List<CustomWeaponConfig> CUSTOM_WEAPON_LISTS = Lists.<CustomWeaponConfig>newArrayList();
	public static final List<CustomArmorConfig> CUSTOM_ARMOR_LISTS = Lists.<CustomArmorConfig>newArrayList();
	
	public static void init(ForgeConfigSpec.Builder config, Map<String, Object> dynamicConfigMap) {
		String weaponKey = "custom_weaponry";
		
		if (dynamicConfigMap.get(weaponKey) != null) {
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)dynamicConfigMap.get(weaponKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
		    
			for (Map.Entry<String, Object> entry : entries) {
				ConfigValue<String> registryName = config.define(String.format("%s.%s.registry_name", weaponKey, entry.getKey()), "broken_item");
				ConfigValue<WeaponType> weaponType = config.defineEnum(String.format("%s.%s.weapon_type", weaponKey, entry.getKey()), WeaponType.SWORD);
				ConfigValue<Double> impactOnehand;
				ConfigValue<Double> armorIgnoranceOnehand;
				ConfigValue<Integer> hitAtOnceOnehand;
				ConfigValue<Double> impactTwohand;
				ConfigValue<Double> armorIgnoranceTwohand;
				ConfigValue<Integer> hitAtOnceTwohand;
				
				boolean containOnehand = ((AbstractCommentedConfig)entry.getValue()).contains("onehand");
				boolean containTwohand = ((AbstractCommentedConfig)entry.getValue()).contains("twohand");
				
				if (!(containOnehand || containTwohand)) {
					impactOnehand = config.define(String.format("%s.%s.impact", weaponKey, entry.getKey()), 0.5D);
					armorIgnoranceOnehand = config.define(String.format("%s.%s.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					hitAtOnceOnehand = config.define(String.format("%s.%s.hit_at_once", weaponKey, entry.getKey()), 1);
					impactTwohand = config.define(String.format("%s.%s.impact", weaponKey, entry.getKey()), 0.5D);
					armorIgnoranceTwohand = config.define(String.format("%s.%s.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					hitAtOnceTwohand = config.define(String.format("%s.%s.hit_at_once", weaponKey, entry.getKey()), 1);
				} else {
					impactOnehand = config.define(String.format("%s.%s.onehand.impact", weaponKey, entry.getKey()), 0.5D);
					armorIgnoranceOnehand = config.define(String.format("%s.%s.onehand.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					hitAtOnceOnehand = config.define(String.format("%s.%s.onehand.hit_at_once", weaponKey, entry.getKey()), 1);
					impactTwohand = config.define(String.format("%s.%s.twohand.impact", weaponKey, entry.getKey()), 0.5D);
					armorIgnoranceTwohand = config.define(String.format("%s.%s.twohand.armor_ignorance", weaponKey, entry.getKey()), 0.0D);
					hitAtOnceTwohand = config.define(String.format("%s.%s.twohand.hit_at_once", weaponKey, entry.getKey()), 1);
				}
				
				if(!entry.getKey().equals("sample")) {
					CUSTOM_WEAPON_LISTS.add(new CustomWeaponConfig(
							registryName, weaponType, impactOnehand, armorIgnoranceOnehand, hitAtOnceOnehand, impactTwohand, armorIgnoranceTwohand, hitAtOnceTwohand
					));
				}
			}
		}
		
		String armorKey = "custom_armor";
		if (dynamicConfigMap.get(armorKey) != null) {
			List<Map.Entry<String, Object>> entries = new LinkedList<>(((AbstractCommentedConfig)dynamicConfigMap.get(armorKey)).valueMap().entrySet());
		    Collections.sort(entries, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
			for (Map.Entry<String, Object> entry : entries) {
				ConfigValue<String> registryName = config.define(String.format("%s.%s.registry_name", armorKey, entry.getKey()), "broken_item");
				ConfigValue<Double> weight = config.define(String.format("%s.%s.weight", armorKey, entry.getKey()), 0.0D);
				ConfigValue<Double> stunArmor = config.define(String.format("%s.%s.stun_armor", armorKey, entry.getKey()), 0.0D);
				
				if(!entry.getKey().equals("sample")) {
					CUSTOM_ARMOR_LISTS.add(new CustomArmorConfig(registryName, stunArmor, weight));
				}
			}
		}
	}
	
	public static class CustomArmorConfig {
		private ConfigValue<String> registryName;
		private ConfigValue<Double> stunArmor;
		private ConfigValue<Double> weight;
		
		public CustomArmorConfig(ConfigValue<String> registryName, ConfigValue<Double> stunArmor, ConfigValue<Double> weight) {
			this.registryName = registryName;
			this.stunArmor = stunArmor;
			this.weight = weight;
		}
		
		public String getRegistryName() {
			return this.registryName.get();
		}
		
		public Double getStunArmor() {
			return this.stunArmor.get();
		}
		
		public Double getWeight() {
			return this.weight.get();
		}
	}
	
	public static class CustomWeaponConfig {
		private ConfigValue<String> registryName;
		private ConfigValue<WeaponType> weaponType;
		private ConfigValue<Double> impactOnehand;
		private ConfigValue<Double> armorIgnoranceOnehand;
		private ConfigValue<Integer> hitAtOnceOnehand;
		private ConfigValue<Double> impactTwohand;
		private ConfigValue<Double> armorIgnoranceTwohand;
		private ConfigValue<Integer> hitAtOnceTwohand;
		
		public CustomWeaponConfig(ConfigValue<String> registryName, ConfigValue<WeaponType> weaponType,
				ConfigValue<Double> impactOnehand, ConfigValue<Double> armorIgnoranceOnehand, ConfigValue<Integer> hitAtOnceOnehand,
				ConfigValue<Double> impactTwohand, ConfigValue<Double> armorIgnoranceTwohand, ConfigValue<Integer> hitAtOnceTwohand) {
			this.registryName = registryName;
			this.weaponType = weaponType;
			this.impactOnehand = impactOnehand;
			this.armorIgnoranceOnehand = armorIgnoranceOnehand;
			this.hitAtOnceOnehand = hitAtOnceOnehand;
			this.impactTwohand = impactTwohand;
			this.armorIgnoranceTwohand = armorIgnoranceTwohand;
			this.hitAtOnceTwohand = hitAtOnceTwohand;
		}
		
		public String getRegistryName() {
			return this.registryName.get();
		}

		public WeaponType getWeaponType() {
			return this.weaponType.get();
		}

		public Double getImpactOnehand() {
			return this.impactOnehand.get();
		}

		public Double getArmorIgnoranceOnehand() {
			return this.armorIgnoranceOnehand.get();
		}

		public Integer getHitAtOnceOnehand() {
			return this.hitAtOnceOnehand.get();
		}

		public Double getImpactTwohand() {
			return this.impactTwohand.get();
		}
		
		public Double getArmorIgnoranceTwohand() {
			return this.armorIgnoranceTwohand.get();
		}

		public Integer getHitAtOnceTwohand() {
			return this.hitAtOnceTwohand.get();
		}
	}
	
	public static enum WeaponType {
		AXE((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.AXE, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.GUILLOTINE_AXE);
			return cap;
		}),
		FIST((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.FIST, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLUNT_HIT,
					Colliders.fist, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.FIST_AUTO_1, Animations.FIST_AUTO_2, Animations.FIST_AUTO_3, Animations.FIST_DASH);
			return cap;
		}),
		HOE((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.HOE, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.TOOL_AUTO_1, Animations.TOOL_AUTO_2, Animations.TOOL_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			return cap;
		}),
		PICKAXE((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.PICKAXE, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			return cap;
		}),
		SHOVEL((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.SHOVEL, (playerdata)->WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLUNT_HIT,
					Colliders.tools, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.AXE_AUTO1, Animations.AXE_AUTO2, Animations.AXE_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			return cap;
		}),
		SWORD((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.SWORD, (playerdata)->{
				CapabilityItem itemCap = playerdata.getHeldItemCapability(Hand.OFF_HAND);
				if(itemCap != null && itemCap.getWeaponCategory() == WeaponCategory.SWORD) {
					return WieldStyle.TWO_HAND;
				} else {
					return WieldStyle.ONE_HAND;
				}
			}, null, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.sword, HandProperty.GENERAL);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.SWORD_AUTO_1, Animations.SWORD_AUTO_2, Animations.SWORD_AUTO_3, Animations.SWORD_DASH);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.SWORD_DUAL_AUTO_1, Animations.SWORD_DUAL_AUTO_2, Animations.SWORD_DUAL_AUTO_3, Animations.SWORD_DUAL_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.SWEEPING_EDGE);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.DANCING_EDGE);
			return cap;
		}),
		SPEAR((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.SPEAR, (playerdata)-> 
				playerdata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? WieldStyle.TWO_HAND : WieldStyle.ONE_HAND,
				null, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.spearNarrow, HandProperty.MAINHAND_ONLY);
			cap.addStyleCombo(WieldStyle.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SPEAR_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.HEARTPIERCER);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.SLAUGHTER_STANCE);
			cap.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_HELDING_WEAPON);
			return cap;
		}),
		GREATSWORD((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.GREATSWORD, (playerdata)->WieldStyle.TWO_HAND, null, Sounds.WHOOSH_BIG, Sounds.BLADE_HIT,
					Colliders.greatSword, HandProperty.TWO_HANDED);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.GREATSWORD_AUTO_1, Animations.GREATSWORD_AUTO_2, Animations.GREATSWORD_DASH);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.GIANT_WHIRLWIND);
			cap.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_MASSIVE_HELD);
			cap.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_MASSIVE_HELD);
			cap.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_MASSIVE_HELD);
	    	cap.addLivingMotionChanger(LivingMotion.JUMPING, Animations.BIPED_JUMP_MASSIVE_HELD);
	    	cap.addLivingMotionChanger(LivingMotion.KNEELING, Animations.BIPED_KNEEL_MASSIVE_HELD);
	    	cap.addLivingMotionChanger(LivingMotion.SNEAKING, Animations.BIPED_SNEAK_MASSIVE_HELD);
			return cap;
		}),
		KATANA((item) -> {
			ModWeaponCapability cap = new ModWeaponCapability(WeaponCategory.KATANA, (playerdata)->WieldStyle.TWO_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
					Colliders.katana, HandProperty.TWO_HANDED);
			cap.addStyleCombo(WieldStyle.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH);
			cap.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
			cap.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.SWEEPING_EDGE);
			cap.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_UNSHEATHING);
			cap.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_UNSHEATHING);
			cap.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_UNSHEATHING);
			return cap;
		}),
		BOW((item) -> {
			BowCapability cap = new BowCapability(item);
			return cap;
		}),
		CROSSBOW((item) -> {
			CrossbowCapability cap = new CrossbowCapability(item);
			return cap;
		});
		
		Function<Item, CapabilityItem> capabilitySupplier;
		
		WeaponType(Function<Item, CapabilityItem> capabilitySupplier) {
			this.capabilitySupplier = capabilitySupplier;
		}

		public CapabilityItem get(Item item) {
			return this.capabilitySupplier.apply(item);
		}
	}
}