package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.CrossbowmanGoal;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.item.Items;

public class PillagerData extends AbstractIllagerData<PillagerEntity>
{
	public PillagerData()
	{
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAI()
	{
		super.initAI();
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient)
	{
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK_CROSSBOW);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
	}
	
	@Override
	public void setAIAsUnarmed()
	{
		
	}

	@Override
	public void setAIAsArmed()
	{
		
	}

	@Override
	public void setAIAsMounted(Entity ridingEntity)
	{
		
	}
	
	@Override
	public void setAIAsRange()
	{
		if(this.orgEntity.getHeldItemMainhand().getItem() == Items.CROSSBOW)
		{
			this.orgEntity.goalSelector.addGoal(3, new CrossbowmanGoal<>(this, this.orgEntity, 1.0D, 8.0F));
		}
	}
}