package maninhouse.epicfight.entity.ai;

import java.util.EnumSet;

import maninhouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CrossbowmanGoal<T extends MonsterEntity & IRangedAttackMob & ICrossbowUser> extends Goal
{
	private final T ownerEntity;
	private CrossbowState crossbowState = CrossbowState.UNCHARGED;
	private final double moveSpeed;
	private final float range;
	private int field_220752_e;
	private int field_220753_f;

	public CrossbowmanGoal(BipedMobData<?> owner, T p_i50322_1_, double p_i50322_2_, float p_i50322_4_)
	{
		this.ownerEntity = p_i50322_1_;
		this.moveSpeed = p_i50322_2_;
		this.range = p_i50322_4_ * p_i50322_4_;
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		return this.func_220746_h() && this.func_220745_g();
	}

	private boolean func_220745_g()
	{
		return this.ownerEntity.canEquip(Items.CROSSBOW);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting()
	{
		return this.func_220746_h() && (this.shouldExecute() || !this.ownerEntity.getNavigator().noPath()) && this.func_220745_g();
	}

	private boolean func_220746_h()
	{
		return this.ownerEntity.getAttackTarget() != null && this.ownerEntity.getAttackTarget().isAlive();
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void resetTask()
	{
		super.resetTask();
		this.ownerEntity.setAggroed(false);
		this.ownerEntity.setAttackTarget((LivingEntity) null);
		this.field_220752_e = 0;
		if (this.ownerEntity.isHandActive())
		{
			this.ownerEntity.resetActiveHand();
			((ICrossbowUser) this.ownerEntity).setCharging(false);
			CrossbowItem.setCharged(this.ownerEntity.getActiveItemStack(), false);
			
		}
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(-1, ownerEntity.getEntityId(), 0.0F, false), ownerEntity);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick()
	{
		LivingEntity livingentity = this.ownerEntity.getAttackTarget();
		if (livingentity != null) {
			boolean flag = this.ownerEntity.getEntitySenses().canSee(livingentity);
			boolean flag1 = this.field_220752_e > 0;
			if (flag != flag1)
			{
				this.field_220752_e = 0;
			}

			if (flag)
			{
				++this.field_220752_e;
			}
			else
			{
				--this.field_220752_e;
			}

			double d0 = this.ownerEntity.getDistanceSq(livingentity);
			boolean flag2 = (d0 > (double) this.range || this.field_220752_e < 5) && this.field_220753_f == 0;
			if (flag2)
			{
				this.ownerEntity.getNavigator().tryMoveToEntityLiving(livingentity,
						this.func_220747_j() ? this.moveSpeed : this.moveSpeed * 0.5D);
			}
			else
			{
				this.ownerEntity.getNavigator().clearPath();
			}

			this.ownerEntity.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
			if (this.crossbowState == CrossbowState.UNCHARGED)
			{
				if (!flag2)
				{
					this.ownerEntity.setActiveHand(ProjectileHelper.getHandWith(this.ownerEntity, Items.CROSSBOW));
					this.crossbowState = CrossbowState.CHARGING;
					((ICrossbowUser) this.ownerEntity).setCharging(true);
				}
			}
			else if
			(this.crossbowState == CrossbowState.CHARGING)
			{
				if (!this.ownerEntity.isHandActive())
				{
					this.crossbowState = CrossbowState.UNCHARGED;
				}

				int i = this.ownerEntity.getItemInUseMaxCount();
				
				ItemStack itemstack = this.ownerEntity.getActiveItemStack();
				if(i == 7)
				{
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_RELOAD.getId(), ownerEntity.getEntityId(), 0.0F, true), ownerEntity);
				}
				
				if (i >= CrossbowItem.getChargeTime(itemstack))
				{
					this.ownerEntity.stopActiveHand();
					this.crossbowState = CrossbowState.CHARGED;
					this.field_220753_f = 20 + this.ownerEntity.getRNG().nextInt(20);
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_AIM.getId(), ownerEntity.getEntityId(), 0.0F, true), ownerEntity);
					((ICrossbowUser) this.ownerEntity).setCharging(false);
				}
			}
			else if (this.crossbowState == CrossbowState.CHARGED)
			{
				--this.field_220753_f;
				if (this.field_220753_f == 0)
				{
					this.crossbowState = CrossbowState.READY_TO_ATTACK;
				}
			}
			else if (this.crossbowState == CrossbowState.READY_TO_ATTACK && flag)
			{
				ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(Animations.BIPED_CROSSBOW_SHOT.getId(), ownerEntity.getEntityId(), 0.0F, true), ownerEntity);
				((IRangedAttackMob) this.ownerEntity).attackEntityWithRangedAttack(livingentity, 1.0F);
				ItemStack itemstack1 = this.ownerEntity.getHeldItem(ProjectileHelper.getHandWith(this.ownerEntity, Items.CROSSBOW));
				CrossbowItem.setCharged(itemstack1, false);
				this.crossbowState = CrossbowState.UNCHARGED;
			}

		}
	}

	private boolean func_220747_j()
	{
		return this.crossbowState == CrossbowState.UNCHARGED;
	}

	static enum CrossbowState
	{
		UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK;
	}
}