package maninhouse.epicfight.client.capabilites.entity;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSReqPlayerInfo;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemoteClientPlayerData<T extends AbstractClientPlayerEntity> extends PlayerData<T> {
	protected float prevYaw;
	protected float bodyYaw;
	protected float prevBodyYaw;
	private ItemStack prevHeldItem;
	private ItemStack prevHeldItemOffHand;
	private boolean swingArm;
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.prevHeldItem = ItemStack.EMPTY;
		this.prevHeldItemOffHand = ItemStack.EMPTY;
		if(!(this instanceof ClientPlayerData)) {
			ModNetworkManager.sendToServer(new CTSReqPlayerInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	public void updateMotion() {
		if (this.orgEntity.getHealth() <= 0.0F) {
			currentMotion = LivingMotion.DEATH;
		} else if (orgEntity.isElytraFlying()) {
			currentMotion = LivingMotion.FLYING;
		} else if (orgEntity.getRidingEntity() != null) {
			currentMotion = LivingMotion.MOUNT;
		} else if (orgEntity.getPose() == Pose.SWIMMING && !this.orgEntity.isHandActive()) {
			currentMotion = LivingMotion.SWIMMING;
		} else {
			AnimatorClient animator = getClientAnimator();

			if (orgEntity.canSwim() && orgEntity.getMotion().y < -0.005)
				currentMotion = LivingMotion.FLOATING;
			else if(orgEntity.getMotion().y < -0.55F)
				currentMotion = LivingMotion.FALL;
			else if (orgEntity.limbSwingAmount > 0.01F) {
				if(orgEntity.isSneaking())
					currentMotion = LivingMotion.SNEAKING;
				else if (orgEntity.isSprinting())
					currentMotion = LivingMotion.RUNNING;
				else
					currentMotion = LivingMotion.WALKING;

				if (orgEntity.moveForward > 0)
					animator.setReverse(false, this.currentMotion);
				else if (orgEntity.moveForward < 0)
					animator.setReverse(true, this.currentMotion);
			} else {
				animator.setReverse(false, this.currentMotion);
				if (orgEntity.isSneaking())
					currentMotion = LivingMotion.KNEELING;
				else
					currentMotion = LivingMotion.IDLE;
			}
		}

		if (this.orgEntity.isHandActive() && orgEntity.getItemInUseCount() > 0) {
			UseAction useAction = this.orgEntity.getHeldItem(this.orgEntity.getActiveHand()).getUseAction();

			if (useAction == UseAction.BLOCK)
				currentMixMotion = LivingMotion.BLOCKING;
			else if (useAction == UseAction.BOW)
				currentMixMotion = LivingMotion.AIMING;
			else if (useAction == UseAction.CROSSBOW)
				currentMixMotion = LivingMotion.RELOADING;
			else if (useAction == UseAction.SPEAR)
				currentMixMotion = LivingMotion.AIMING;
			else
				currentMixMotion = LivingMotion.NONE;
		} else {
			if (CrossbowItem.isCharged(this.orgEntity.getHeldItemMainhand()))
				currentMixMotion = LivingMotion.AIMING;
			else if (this.getClientAnimator().prevAiming())
				this.playReboundAnimation();
			else
				currentMixMotion = LivingMotion.NONE;
		}
	}
	
	@Override
	protected void updateOnClient() {
		this.prevYaw = this.yaw;
		this.prevBodyYaw = this.bodyYaw;
		this.bodyYaw = this.inaction ? this.orgEntity.rotationYaw : this.orgEntity.prevRenderYawOffset;
		
		boolean isMainHandChanged = prevHeldItem.getItem() != this.orgEntity.inventory.getCurrentItem().getItem();
		boolean isOffHandChanged = prevHeldItemOffHand.getItem() != this.orgEntity.inventory.offHandInventory.get(0).getItem();
		
		if(isMainHandChanged || isOffHandChanged) {
			onHeldItemChange(this.getHeldItemCapability(Hand.MAIN_HAND), this.getHeldItemCapability(Hand.OFF_HAND));
			if(isMainHandChanged)
				prevHeldItem = this.orgEntity.inventory.getCurrentItem();
			if(isOffHandChanged)
				prevHeldItemOffHand = this.orgEntity.inventory.offHandInventory.get(0);
		}
		
		super.updateOnClient();
		
		if(this.orgEntity.deathTime == 1) {
			this.getClientAnimator().playDeathAnimation();
		}
		
		if (this.swingArm != orgEntity.isSwingInProgress) {
			if(!this.swingArm) {
				this.getClientAnimator().playMixLayerAnimation(Animations.BIPED_DIG);
			} else {
				this.getClientAnimator().offMixLayer(false);
			}
			
			this.swingArm = orgEntity.isSwingInProgress;
		}
	}

	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		this.getClientAnimator().resetMixMotion();
		this.getClientAnimator().offMixLayer(false);
		this.cancelUsingItem();
	}

	protected void playReboundAnimation() {
		this.getClientAnimator().playReboundAnimation();
	}

	@Override
	public void playAnimationSynchronize(int id, float modifyTime) {
		//
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return this.orgEntity.getSkinType().equals("slim") ? modelDB.ENTITY_BIPED_SLIM_ARM : modelDB.ENTITY_BIPED;
	}

	@Override
	public VisibleMatrix4f getHeadMatrix(float partialTick) {
		T entity = getOriginalEntity();
		
        float yaw;
        float pitch = 0;
        float prvePitch = 0;
        
		if (inaction || entity.getRidingEntity() != null) {
	        yaw = 0;
		} else {
			float f = MathUtils.interpolateRotation(this.prevBodyYaw, this.bodyYaw, partialTick);
			float f1 = MathUtils.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTick);
	        yaw = f1 - f;
		}
        
		if (!orgEntity.isElytraFlying()) {
			prvePitch = entity.prevRotationPitch;
			pitch = entity.rotationPitch;
		}
        
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, prvePitch, pitch, yaw, yaw, partialTick, 1, 1, 1);
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTick) {
		LivingEntity entity = getOriginalEntity();

		if (orgEntity.isSpinAttacking()) {
			VisibleMatrix4f mat = MathUtils.getModelMatrixIntegrated((float)entity.lastTickPosX, (float)entity.getPosX(), (float)entity.lastTickPosY, (float)entity.getPosY(),
					(float)entity.lastTickPosZ, (float)entity.getPosZ(), 0, 0, 0, 0, partialTick, 1, 1, 1);
			float yawDegree = MathUtils.interpolateRotation(orgEntity.prevRotationYaw, orgEntity.rotationYaw, partialTick);
			float pitchDegree = MathUtils.interpolateRotation(orgEntity.prevRotationPitch, orgEntity.rotationPitch, partialTick) + 90.0F;
			VisibleMatrix4f.rotate((float)-Math.toRadians(yawDegree), new Vec3f(0F, 1F, 0F), mat, mat);
			VisibleMatrix4f.rotate((float)-Math.toRadians(pitchDegree), new Vec3f(1F, 0F, 0F), mat, mat);
			VisibleMatrix4f.rotate((float)Math.toRadians((orgEntity.ticksExisted + partialTick) * -55.0), new Vec3f(0F, 1F, 0F), mat, mat);
            
            return mat;
		} else if (orgEntity.isElytraFlying()) {
			VisibleMatrix4f mat = MathUtils.getModelMatrixIntegrated((float)entity.lastTickPosX, (float)entity.getPosX(), (float)entity.lastTickPosY, (float)entity.getPosY(),
					(float)entity.lastTickPosZ, (float)entity.getPosZ(), 0, 0, 0, 0, partialTick, 1, 1, 1);
			
			VisibleMatrix4f.rotate((float)-Math.toRadians(entity.renderYawOffset), new Vec3f(0F, 1F, 0F), mat, mat);
			
            float f = (float)orgEntity.getTicksElytraFlying() + Minecraft.getInstance().getRenderPartialTicks();
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            VisibleMatrix4f.rotate((float)Math.toRadians(f1 * (-90F - orgEntity.rotationPitch)), new Vec3f(1F, 0F, 0F), mat, mat);
            
            Vector3d vec3d = orgEntity.getLook(Minecraft.getInstance().getRenderPartialTicks());
            Vector3d vec3d1 = orgEntity.getMotion();
            
            double d0 = vec3d1.x * vec3d1.x + vec3d1.z * vec3d1.z;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            
			if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                
                VisibleMatrix4f.rotate((float)Math.toRadians((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI), new Vec3f(0F, 1F, 0F), mat, mat);
            }
			
            return mat;
		} else {
			float yaw;
			float prevRotYaw;
			float rotyaw;
			float prevPitch = 0;
			float pitch = 0;
			
			if (orgEntity.getRidingEntity() instanceof LivingEntity) {
				LivingEntity ridingEntity = (LivingEntity)orgEntity.getRidingEntity();
				prevRotYaw = ridingEntity.prevRenderYawOffset;
				rotyaw = ridingEntity.renderYawOffset;
			} else {
				yaw = inaction ? MathUtils.interpolateRotation(this.prevYaw, this.yaw, partialTick) : 0;
				prevRotYaw = this.prevBodyYaw + yaw;
				rotyaw = this.bodyYaw + yaw;
			}
			
			if (!this.isInaction() && orgEntity.getPose() == Pose.SWIMMING) {
				float f = this.orgEntity.getSwimAnimation(partialTick);
				float f3 = this.orgEntity.isInWater() ? this.orgEntity.rotationPitch : 0;
		        float f4 = MathHelper.lerp(f, 0.0F, f3);
		        prevPitch = f4;
		        pitch = f4;
			}
			
			return MathUtils.getModelMatrixIntegrated((float)entity.lastTickPosX, (float)entity.getPosX(), (float)entity.lastTickPosY, (float)entity.getPosY(),
					(float)entity.lastTickPosZ, (float)entity.getPosZ(), prevPitch, pitch, prevRotYaw, rotyaw, partialTick, 1, 1, 1);
		}
	}
}