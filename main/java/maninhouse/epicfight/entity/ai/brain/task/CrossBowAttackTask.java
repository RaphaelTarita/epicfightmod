package maninhouse.epicfight.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;

import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class CrossBowAttackTask<E extends MobEntity & ICrossbowUser> extends Task<E>
{
	private int cooldown;
	private CrossBowAttackTask.Status status = CrossBowAttackTask.Status.UNCHARGED;

	public CrossBowAttackTask()
	{
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.VALUE_PRESENT), 1200);
	}
	
	@Override
	protected boolean shouldExecute(ServerWorld worldIn, E owner)
	{
		LivingEntity livingentity = getAttackTarget(owner);
		return owner.canEquip(Items.CROSSBOW) && BrainUtil.isMobVisible(owner, livingentity) && BrainUtil.canFireAtTarget(owner, livingentity, 0);
	}
	
	@Override
	protected boolean shouldContinueExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn)
	{
		return entityIn.getBrain().hasMemory(MemoryModuleType.ATTACK_TARGET) && this.shouldExecute(worldIn, entityIn);
	}
	
	@Override
	protected void updateTask(ServerWorld worldIn, E owner, long gameTime)
	{
		LivingEntity livingentity = getAttackTarget(owner);
		this.setLookTargetMemory(owner, livingentity);
		this.func_233888_a_(owner, livingentity);
	}
	
	@Override
	protected void resetTask(ServerWorld worldIn, E entityIn, long gameTimeIn)
	{
		if (entityIn.isHandActive())
		{
			entityIn.resetActiveHand();
		}
		
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(-1, entityIn.getEntityId(), 0.0F, false), entityIn);
		
		if (entityIn.canEquip(Items.CROSSBOW))
		{
			entityIn.setCharging(false);
			CrossbowItem.setCharged(entityIn.getActiveItemStack(), false);
		}
	}
	
	private void func_233888_a_(E taskOwner, LivingEntity target)
	{
		if (this.status == CrossBowAttackTask.Status.UNCHARGED)
		{
			taskOwner.setActiveHand(ProjectileHelper.getHandWith(taskOwner, Items.CROSSBOW));
			this.status = CrossBowAttackTask.Status.CHARGING;
			taskOwner.setCharging(true);
		}
		else if (this.status == CrossBowAttackTask.Status.CHARGING)
		{
			if (!taskOwner.isHandActive())
				this.status = CrossBowAttackTask.Status.UNCHARGED;

			int i = taskOwner.getItemInUseMaxCount();
			if(i == 7)
				ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_RELOAD.getId(), taskOwner.getEntityId(), 0.0F, true), taskOwner);
			
			ItemStack itemstack = taskOwner.getActiveItemStack();
			if(i >= CrossbowItem.getChargeTime(itemstack))
			{
				taskOwner.stopActiveHand();
				this.status = CrossBowAttackTask.Status.CHARGED;
				this.cooldown = 20 + taskOwner.getRNG().nextInt(20);
				taskOwner.setCharging(false);
				ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_AIM.getId(), taskOwner.getEntityId(), 0.0F, true), taskOwner);
			}
		}
		else if (this.status == CrossBowAttackTask.Status.CHARGED)
		{
			--this.cooldown;
			if (this.cooldown == 0)
				this.status = CrossBowAttackTask.Status.READY_TO_ATTACK;
		}
		else if (this.status == CrossBowAttackTask.Status.READY_TO_ATTACK)
		{
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_SHOT.getId(), taskOwner.getEntityId(), 0.0F, true), taskOwner);
			taskOwner.attackEntityWithRangedAttack(target, 1.0F);
			ItemStack itemstack1 = taskOwner.getHeldItem(ProjectileHelper.getHandWith(taskOwner, Items.CROSSBOW));
			CrossbowItem.setCharged(itemstack1, false);
			this.status = CrossBowAttackTask.Status.UNCHARGED;
		}
	}
	
	private void setLookTargetMemory(MobEntity p_233889_1_, LivingEntity p_233889_2_)
	{
		p_233889_1_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_233889_2_, true));
	}

	private static LivingEntity getAttackTarget(LivingEntity p_233887_0_)
	{
		return p_233887_0_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	static enum Status
	{
		UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;
	}
}