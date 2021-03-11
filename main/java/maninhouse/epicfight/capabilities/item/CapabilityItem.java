package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.skill.SkillSlot;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class CapabilityItem {
	protected static List<StaticAnimation> commonAutoAttackMotion;
	protected Map<Supplier<Attribute>, AttributeModifier> oneHandedStyleDamageAttribute;
	protected Map<Supplier<Attribute>, AttributeModifier> twoHandedStyleDamageAttribute;
	protected final WeaponCategory weaponCategory;
	
	static {
		commonAutoAttackMotion = new ArrayList<StaticAnimation> ();
		commonAutoAttackMotion.add(Animations.FIST_AUTO_1);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_2);
		commonAutoAttackMotion.add(Animations.FIST_AUTO_3);
		commonAutoAttackMotion.add(Animations.FIST_DASH);
	}
	
	public static List<StaticAnimation> getBasicAutoAttackMotion() {
		return commonAutoAttackMotion;
	}

	protected void loadClientThings() {
		
	}
	
	public CapabilityItem(WeaponCategory category) {
		if (EpicFightMod.isPhysicalClient()) {
			loadClientThings();
		}
		oneHandedStyleDamageAttribute = new HashMap<Supplier<Attribute>, AttributeModifier>();
		this.weaponCategory = category;
		registerAttribute();
	}
	
	public CapabilityItem(Item material, WeaponCategory category) {
		oneHandedStyleDamageAttribute = new HashMap<Supplier<Attribute>, AttributeModifier>();
		this.weaponCategory = category;
	}

	protected void registerAttribute() {

	}

	public void modifyItemTooltip(List<ITextComponent> itemTooltip, boolean isOffhandEmpty) {
		if(this.isTwoHanded()) {
			itemTooltip.add(1, new TranslationTextComponent("attribute.name."+EpicFightMod.MODID+".twohanded").mergeStyle(TextFormatting.DARK_GRAY));
		} else if(!this.canUsedOffhand()) {
			itemTooltip.add(1, new TranslationTextComponent("attribute.name."+EpicFightMod.MODID+".mainhand_only").mergeStyle(TextFormatting.DARK_GRAY));
		}
		
		Map<Supplier<Attribute>, AttributeModifier> attribute = this.getDamageAttributesInCondition(isOffhandEmpty);
		
		if(attribute != null) {
			for(Map.Entry<Supplier<Attribute>, AttributeModifier> attr : attribute.entrySet()) {
				itemTooltip.add(new TranslationTextComponent(attr.getKey().get().getAttributeName(), 
						ItemStack.DECIMALFORMAT.format(attr.getValue().getAmount() + attr.getKey().get().getDefaultValue())));
			}
			
			if(!attribute.keySet().contains(ModAttributes.HIT_AT_ONCE)) {
				itemTooltip.add(new TranslationTextComponent(ModAttributes.HIT_AT_ONCE.get().getAttributeName(), 
						ItemStack.DECIMALFORMAT.format(ModAttributes.HIT_AT_ONCE.get().getDefaultValue())));
			}
		}
	}
	
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return getBasicAutoAttackMotion();
	}

	public List<StaticAnimation> getMountAttackMotion() {
		return null;
	}

	public boolean hasSpecialAttack(PlayerData<?> playerdata) {
		return false;
	}

	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		return null;
	}

	public Skill getPassiveSkill() {
		return null;
	}
	
	public void onHeld(PlayerData<?> playerdata) {
		if (this.hasSpecialAttack(playerdata)) {
			Skill skill = this.getSpecialAttack(playerdata);
			SkillContainer skillContainer = playerdata.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
			
			if(skillContainer.getContaining() != skill) {
				skillContainer.setSkill(skill);
			}
		}
		
		Skill skill = this.getPassiveSkill();
		SkillContainer skillContainer = playerdata.getSkill(SkillSlot.WEAPON_GIMMICK);
		
		if(skill == null) {
			skillContainer.setSkill(null);
		} else {
			if(skillContainer.getContaining() != skill) {
				skillContainer.setSkill(skill);
			}
		}
	}
	
	public SoundEvent getSmashingSound() {
		return Sounds.WHOOSH;
	}

	public SoundEvent getHitSound() {
		return Sounds.BLUNT_HIT;
	}

	public Collider getWeaponCollider() {
		return Colliders.fist;
	}

	public HitParticleType getHitParticle() {
		return Particles.HIT_BLUNT.get();
	}
	
	public Map<Supplier<Attribute>, AttributeModifier> getDamageAttributesInCondition(boolean offhandEmtpy) {
		return (isTwoHanded() || (!canUsedOffhand() && offhandEmtpy)) ? twoHandedStyleDamageAttribute : oneHandedStyleDamageAttribute;
	}
	
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, LivingData<?> entitydata) {
		Multimap<Attribute, AttributeModifier> map = HashMultimap.<Attribute, AttributeModifier>create();
		
		if(entitydata != null) {
			Map<Supplier<Attribute>, AttributeModifier> modifierMap = this.getDamageAttributesInCondition(entitydata.getOriginalEntity().getHeldItemOffhand().isEmpty());
			if(modifierMap != null) {
				for(Entry<Supplier<Attribute>, AttributeModifier> entry : modifierMap.entrySet()) {
					map.put(entry.getKey().get(), entry.getValue());
				}
			}
		}
		
		return map;
    }
	
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		return null;
	}

	public boolean canUsedOffhand() {
		return this.isTwoHanded() ? false : true;
	}

	public boolean isTwoHanded() {
		return false;
	}

	public boolean canCompatibleWith(ItemStack item) {
		return !isTwoHanded() && !item.isEmpty();
	}

	public boolean canUseOnMount() {
		return !this.isTwoHanded();
	}
	
	public enum WeaponCategory {
		NONE_WEAON, AXE, FIST, GREATSWORD, HOE, PICKAXE, SHOVEL, SWORD, KATANA, SPEAR, BOW, CROSSBOW
	}
}