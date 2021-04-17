package maninhouse.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.property.Property.DamageProperty;
import maninhouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSExecuteSkill;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.ValueCorrector;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class SpecialAttackSkill extends Skill {
	protected List<Map<DamageProperty<?>, Object>> properties;

	public SpecialAttackSkill(float restriction, String skillName) {
		this(restriction, 0, skillName);
	}
	
	public SpecialAttackSkill(float restriction, int duration, String skillName) {
		super(SkillSlot.WEAPON_SPECIAL_ATTACK, restriction, duration, true, skillName);
		this.properties = Lists.<Map<DamageProperty<?>, Object>>newArrayList();
	}
	
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		ModNetworkManager.sendToServer(new CTSExecuteSkill(this.slot.getIndex(), true, args));
	}
	
	@Override
	public float getRegenTimePerTick(PlayerData<?> player) {
		return 0;
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer) {
		CapabilityItem item = executer.getHeldItemCapability(Hand.MAIN_HAND);
		if (item != null) {
			Skill skill = item.getSpecialAttack(executer);
			return skill == this && executer.getOriginalEntity().getRidingEntity() == null;
		}
		
		return false;
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isElytraFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		
		list.add(new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.registryName.getPath()).mergeStyle(TextFormatting.WHITE)
				.append(new StringTextComponent(String.format("[%.0f]", this.cooldown)).mergeStyle(TextFormatting.AQUA)));
		list.add(new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.registryName.getPath() + "_tooltip").mergeStyle(TextFormatting.DARK_GRAY));
		
		return list;
	}
	
	protected void generateTooltipforPhase(List<ITextComponent> list, ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap, Map<DamageProperty<?>, Object> propertyMap, String title) {
		Multimap<Attribute, AttributeModifier> attributes = itemStack.getAttributeModifiers(EquipmentSlotType.MAINHAND);
		Multimap<Attribute, AttributeModifier> capAttributes = cap.getAttributeModifiers(EquipmentSlotType.MAINHAND, playerCap);
		double damage = playerCap.getOriginalEntity().getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
		double armorNegation = playerCap.getOriginalEntity().getAttribute(ModAttributes.ARMOR_NEGATION.get()).getBaseValue();
		double impact = playerCap.getOriginalEntity().getAttribute(ModAttributes.IMPACT.get()).getBaseValue();
		double maxStrikes = playerCap.getOriginalEntity().getAttribute(ModAttributes.MAX_STRIKES.get()).getBaseValue();
		ValueCorrector damageCorrector = ValueCorrector.base();
		ValueCorrector armorNegationCorrector = ValueCorrector.base();
		ValueCorrector impactCorrector = ValueCorrector.base();
		ValueCorrector maxStrikesCorrector = ValueCorrector.base();
		
		for (AttributeModifier modifier : attributes.get(Attributes.ATTACK_DAMAGE)) {
			damage += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(ModAttributes.ARMOR_NEGATION.get())) {
			armorNegation += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(ModAttributes.IMPACT.get())) {
			impact += modifier.getAmount();
		}
		for (AttributeModifier modifier : capAttributes.get(ModAttributes.MAX_STRIKES.get())) {
			maxStrikes += modifier.getAmount();
		}
		
		this.getProperty(DamageProperty.DAMAGE, propertyMap).ifPresent(damageCorrector::merge);
		this.getProperty(DamageProperty.ARMOR_NEGATION, propertyMap).ifPresent(armorNegationCorrector::merge);
		this.getProperty(DamageProperty.IMPACT, propertyMap).ifPresent(impactCorrector::merge);
		this.getProperty(DamageProperty.MAX_STRIKES, propertyMap).ifPresent(maxStrikesCorrector::merge);
		
		damage = damageCorrector.get((float)damage);
		armorNegation = armorNegationCorrector.get((float)armorNegation);
		impact = impactCorrector.get((float)impact);
		maxStrikes = maxStrikesCorrector.get((float)maxStrikes);
		
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(title).mergeStyle(TextFormatting.UNDERLINE).mergeStyle(TextFormatting.GRAY));
		list.add(new StringTextComponent(""));
		list.add(new StringTextComponent(TextFormatting.RED + ItemStack.DECIMALFORMAT.format(damage) + TextFormatting.DARK_GRAY + " Damage"));
		
		if (armorNegation != 0.0D) {
			list.add(new StringTextComponent(TextFormatting.GOLD + ItemStack.DECIMALFORMAT.format(armorNegation) + TextFormatting.DARK_GRAY + " Armor negation"));
		}
		if (impact != 0.0D) {
			list.add(new StringTextComponent(TextFormatting.AQUA + ItemStack.DECIMALFORMAT.format(impact) + TextFormatting.DARK_GRAY + " Impact"));
		}
		
		list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Hit " + TextFormatting.WHITE + ItemStack.DECIMALFORMAT.format(maxStrikes) + TextFormatting.DARK_GRAY + " Enemies per Swing"));
		
		Optional<StunType> stunOption = this.getProperty(DamageProperty.STUN_TYPE, propertyMap);
		
		stunOption.ifPresent((stunType) -> {
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Apply " + stunType.toString()));
		});
		if (!stunOption.isPresent()) {
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "Apply " + StunType.SHORT.toString()));
		}	
	}
	
	@SuppressWarnings("unchecked")
	protected <V> Optional<V> getProperty(DamageProperty<V> propertyType, Map<DamageProperty<?>, Object> map) {
		return (Optional<V>) Optional.ofNullable(map.get(propertyType));
	}
	
	public SpecialAttackSkill newPropertyLine() {
		this.properties.add(Maps.<DamageProperty<?>, Object>newHashMap());
		return this;
	}
	
	public <T> SpecialAttackSkill addProperty(DamageProperty<T> attribute, T object) {
		this.properties.get(properties.size()-1).put(attribute, object);
		return this;
	}
	
	public abstract SpecialAttackSkill registerPropertiesToAnimation();
}