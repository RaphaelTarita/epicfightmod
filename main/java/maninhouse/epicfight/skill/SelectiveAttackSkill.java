package maninhouse.epicfight.skill;

import java.util.function.Function;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.network.PacketBuffer;

public class SelectiveAttackSkill extends SpecialAttackSkill
{
	protected final StaticAnimation[] attackAnimations;
	protected final Function<ServerPlayerData, Integer> selector;
	
	public SelectiveAttackSkill(SkillSlot index, float restriction, String skillName, Function<ServerPlayerData, Integer> func, StaticAnimation... animations)
	{
		super(index, restriction, skillName, null);
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	public SelectiveAttackSkill(SkillSlot index, float restriction, int duration, String skillName, Function<ServerPlayerData, Integer> func, StaticAnimation... animations)
	{
		super(index, restriction, duration, skillName, null);
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		for(StaticAnimation animation : this.attackAnimations) {
			((AttackAnimation)animation).addProperties(this.propertyMap.entrySet());
		}
		
		return this;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
	
	public int getAnimationInCondition(ServerPlayerData executer)
	{
		return selector.apply(executer);
	}
}