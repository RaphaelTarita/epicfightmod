package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.VindicatorEntity;

public class VindicatorData extends AbstractIllagerData<VindicatorEntity>
{
	public VindicatorData()
	{
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.ANGRY, Animations.VINDICATOR_IDLE_AGGRESSIVE);
		animatorClient.addLivingAnimation(LivingMotion.CHASING, Animations.VINDICATOR_CHASE);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		boolean isAngry = orgEntity.isAggressive();
		
		if(this.orgEntity.getHealth() <= 0.0F) {
			currentMotion = LivingMotion.DEATH;
		} else if (orgEntity.limbSwingAmount > 0.01F) {
			currentMotion = isAngry ? LivingMotion.CHASING : LivingMotion.WALKING;
		} else {
			currentMotion = isAngry ? LivingMotion.ANGRY : LivingMotion.IDLE;
		}
	}
	
	@Override
	public void setAIAsArmed()
	{
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 3.0D, true, MobAttackPatterns.VINDICATOR_PATTERN));
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
	}
	
	@Override
	public void setAIAsUnarmed()
	{
		
	}

	@Override
	public void setAIAsMounted(Entity ridingEntity)
	{
		
	}

	@Override
	public void setAIAsRange()
	{
		
	}
}