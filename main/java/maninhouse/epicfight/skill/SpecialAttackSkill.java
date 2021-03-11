package maninhouse.epicfight.skill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.AnimationProperty;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSExecuteSkill;
import maninhouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpecialAttackSkill extends Skill
{
	protected final StaticAnimation attackAnimation;
	protected Map<AnimationProperty<?>, Object> propertyMap;
	
	public SpecialAttackSkill(SkillSlot index, float restriction, String skillName, StaticAnimation animation)
	{
		this(index, restriction, 0, skillName, animation);
	}
	
	public SpecialAttackSkill(SkillSlot index, float restriction, int duration, String skillName, StaticAnimation animation)
	{
		super(index, restriction, duration, true, skillName);
		this.propertyMap = new HashMap<AnimationProperty<?>, Object> ();
		this.attackAnimation = animation;
	}
	
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args)
	{
		ModNetworkManager.sendToServer(new CTSExecuteSkill(this.slot.getIndex(), true, args));
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		executer.playAnimationSynchronize(attackAnimation, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
	
	@Override
	public float getRegenTimePerTick(PlayerData<?> player)
	{
		return 0;
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer)
	{
		CapabilityItem item = executer.getHeldItemCapability(Hand.MAIN_HAND);
		if(item != null)
		{
			Skill skill = item.getSpecialAttack(executer);
			return skill == this && executer.getOriginalEntity().getRidingEntity() == null;
		}
		
		return false;
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer)
	{
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isElytraFlying() || executer.currentMotion == LivingMotion.FALL || 
			!playerState.canAct());
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<ITextComponent> getTooltip()
	{
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		
		list.add(new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.registryName.getPath()).mergeStyle(TextFormatting.WHITE)
				.append(new StringTextComponent(String.format("[%.0f]", this.cooldown)).mergeStyle(TextFormatting.AQUA)));
		list.add(new TranslationTextComponent("skill." + EpicFightMod.MODID + "." + this.registryName.getPath() + "_tooltip").mergeStyle(TextFormatting.DARK_GRAY));
		
		StringBuilder damageFormat = new StringBuilder("");
		
		if(this.propertyMap.containsKey(AnimationProperty.DAMAGE_MULTIPLIER)) {
			damageFormat.append(String.format("%.0f%%", this.getProperty(AnimationProperty.DAMAGE_MULTIPLIER) * 100.0F));
		} else {
			damageFormat.append("100%");
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.DAMAGE_ADDER)) {
			damageFormat.append(String.format(" + %.0f", this.getProperty(AnimationProperty.DAMAGE_ADDER)));
		}
		damageFormat.append(TextFormatting.DARK_GRAY + " damage");
		list.add(new StringTextComponent(TextFormatting.DARK_RED + damageFormat.toString()));
		
		if(this.propertyMap.containsKey(AnimationProperty.IMPACT)) {
			list.add(new StringTextComponent(String.format(TextFormatting.GOLD + "%.1f" + TextFormatting.DARK_GRAY + " impact", this.getProperty(AnimationProperty.IMPACT))));
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.ARMOR_NEGATION)) {
			list.add(new StringTextComponent(String.format(TextFormatting.GOLD + "%.0f%%" + TextFormatting.DARK_GRAY + " armor negation", this.getProperty(AnimationProperty.ARMOR_NEGATION))));
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.HIT_AT_ONCE)) {
			list.add(new StringTextComponent(String.format(TextFormatting.DARK_GRAY + "hit" + TextFormatting.WHITE + " %d " + TextFormatting.DARK_GRAY + "enemies", this.getProperty(AnimationProperty.HIT_AT_ONCE))));
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.STUN_TYPE)) {
			list.add(new StringTextComponent(TextFormatting.DARK_GRAY + "apply " + (this.getProperty(AnimationProperty.STUN_TYPE).toString())));
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getProperty(AnimationProperty<T> propertyType)
	{
		return (T) this.propertyMap.get(propertyType);
	}
	
	public <T> SpecialAttackSkill addProperty(AnimationProperty<T> attribute, T object) {
		this.propertyMap.put(attribute, object);
		return this;
	}
	
	public SpecialAttackSkill registerPropertiesToAnimation() {
		((AttackAnimation)this.attackAnimation).addProperties(this.propertyMap.entrySet());
		return this;
	}
}