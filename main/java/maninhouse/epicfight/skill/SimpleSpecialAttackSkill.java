package maninhouse.epicfight.skill;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import maninhouse.epicfight.animation.property.Property.DamageProperty;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.animation.types.attack.AttackAnimation.Phase;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class SimpleSpecialAttackSkill extends SpecialAttackSkill {
	protected final StaticAnimation attackAnimation;
	
	public SimpleSpecialAttackSkill(float restriction, String skillName, StaticAnimation animation) {
		this(restriction, 0, skillName, animation);
		
	}
	
	public SimpleSpecialAttackSkill(float restriction, int duration, String skillName, StaticAnimation animation) {
		super(restriction, duration, skillName);
		this.properties = Lists.<Map<DamageProperty<?>, Object>>newArrayList();
		this.attackAnimation = animation;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(this.attackAnimation, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strike:");
		
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		AttackAnimation anim = ((AttackAnimation)this.attackAnimation);
		
		for(Phase phase : anim.phases) {
			phase.addProperties(this.properties.get(0).entrySet());
		}
		
		return this;
	}
}