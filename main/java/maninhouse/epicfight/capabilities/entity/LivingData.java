package maninhouse.epicfight.capabilities.entity;

import java.util.ArrayList;
import java.util.List;

import maninhouse.epicfight.animation.Animator;
import maninhouse.epicfight.animation.AnimatorServer;
import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.DamageType;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public abstract class LivingData<T extends LivingEntity> extends CapabilityEntity<T> {
	private float stunTimeReduction;
	protected boolean inaction = false;
	public LivingMotion currentMotion = LivingMotion.IDLE;
	public LivingMotion currentMixMotion = LivingMotion.NONE;
	protected Animator animator;
	public List<Entity> currentlyAttackedEntity;
	
	@Override
	public void onEntityConstructed(T entityIn) {
		super.onEntityConstructed(entityIn);
		if(this.orgEntity.world.isRemote) {
			this.animator = new AnimatorClient(this);
			this.initAnimator(this.getClientAnimator());
		} else {
			this.animator = new AnimatorServer(this);
		}
		
		this.inaction = false;
		this.currentlyAttackedEntity = new ArrayList<Entity>();
	}
	
	@Override
	public boolean onEntityJoinWorld(T entityIn) {
		if(super.onEntityJoinWorld(entityIn)) {
			this.initAttributes();
			return true;
		} else {
			return false;
		}
	}
	
	protected abstract void initAnimator(AnimatorClient animatorClient);
	public abstract void updateMotion();
	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);
	
	protected void initAttributes() {
		this.orgEntity.getAttribute(ModAttributes.WEIGHT.get()).setBaseValue(this.orgEntity.getMaxHealth() * 2.0D);
		this.orgEntity.getAttribute(ModAttributes.HIT_AT_ONCE.get()).setBaseValue(1.0D);
		this.orgEntity.getAttribute(ModAttributes.IGNORE_DEFENCE.get()).setBaseValue(0.0D);
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.0D);
	}
	
	@Override
	protected void updateOnClient() {
		AnimatorClient animator = getClientAnimator();
		
		if(this.inaction) {
			this.currentMotion = LivingMotion.IDLE;
		} else {
			this.updateMotion();
			if(!animator.compareMotion(currentMotion)) {
				animator.playLoopMotion();
			}
			if(!animator.compareMixMotion(currentMixMotion)) {
				animator.playMixLoopMotion();
			}
		}
	}
	
	@Override
	protected void updateOnServer() {
		if(stunTimeReduction > 0.0F) {
			stunTimeReduction = Math.max(0.0F, stunTimeReduction - 0.05F);
		}
	}
	
	@Override
	public void update() {
		updateInactionState();

		if (isRemote()) {
			updateOnClient();
		} else {
			updateOnServer();
		}

		animator.update();
		if (orgEntity.deathTime == 19) {
			aboutToDeath();
		}
	}

	public void updateInactionState() {
		EntityState state = this.getEntityState();
		if (!state.isCameraRotationLocked() && !state.isMovementLocked()) {
			this.inaction = false;
		} else {
			this.inaction = true;
		}
	}

	protected final void commonBipedCreatureAnimatorInit(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	protected final void commonCreatureUpdateMotion() {
		if(this.orgEntity.getHealth() <= 0.0F) {
			currentMotion = LivingMotion.DEATH;
		} else if(orgEntity.getRidingEntity() != null) {
			currentMotion = LivingMotion.MOUNT;
		} else {
			if(orgEntity.getMotion().y < -0.55F)
				currentMotion = LivingMotion.FALL;
			else if(orgEntity.limbSwingAmount > 0.01F)
				currentMotion = LivingMotion.WALKING;
			else
				currentMotion = LivingMotion.IDLE;
		}
	}
	
	public void cancelUsingItem() {
		this.orgEntity.resetActiveHand();
		net.minecraftforge.event.ForgeEventFactory.onUseItemStop(this.orgEntity, this.orgEntity.getActiveItemStack(), this.orgEntity.getItemInUseCount());
	}
	
	public CapabilityItem getHeldItemCapability(Hand hand) {
		return ModCapabilities.stackCapabilityGetter(this.orgEntity.getHeldItem(hand));
	}

	public boolean isInaction() {
		return this.inaction;
	}
	
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if(this.getEntityState().isInvincible()) {
			if(damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagicDamage()) {
				return false;
			}
		}
		
		return true;
	}
	
	public IExtendedDamageSource getDamageSource(StunType stunType, DamageType damageType, int animationId) {
		return IExtendedDamageSource.causeMobDamage(orgEntity, stunType, damageType, animationId);
	}
	
	public float getDamageToEntity(Entity targetEntity, Hand hand) {
		float damage = 0;
		if (hand == Hand.MAIN_HAND) {
			damage = (float) orgEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
		} else {
			damage = (float) orgEntity.getAttributeValue(ModAttributes.OFFHAND_ATTACK_DAMAGE.get());
		}

		float bonus;
		if (targetEntity instanceof LivingEntity) {
			bonus = EnchantmentHelper.getModifierForCreature(orgEntity.getHeldItem(hand), ((LivingEntity) targetEntity).getCreatureAttribute());
		} else {
			bonus = EnchantmentHelper.getModifierForCreature(orgEntity.getHeldItem(hand), CreatureAttribute.UNDEFINED);
		}

		return damage + bonus;
	}
	
	public boolean hurtEntity(Entity hitTarget, IExtendedDamageSource source, float amount) {
		boolean succed = hitTarget.attackEntityFrom((DamageSource) source, amount);

		if (succed) {
			int j = EnchantmentHelper.getFireAspectModifier(this.orgEntity);
			if (hitTarget instanceof LivingEntity) {
				if (j > 0 && !hitTarget.isBurning())
					hitTarget.setFire(j * 4);
			}
		}

		return succed;
	}
	
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {}
	
	public void setStunTimeReduction() {
		this.stunTimeReduction += (1.0F - stunTimeReduction) * 0.8F;
	}

	public float getStunTimeTimeReduction() {
		return this.stunTimeReduction;
	}

	public void knockBackEntity(Entity entityIn, float power) {
		double d1 = entityIn.getPosX() - this.orgEntity.getPosX();
        double d0;
        
		for (d0 = entityIn.getPosZ() - this.orgEntity.getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }
        
		if (orgEntity.getRNG().nextDouble() >= orgEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) {
        	Vector3d vec = orgEntity.getMotion();
        	
        	orgEntity.isAirBorne = true;
            float f = MathHelper.sqrt(d1 * d1 + d0 * d0);
            
            double x = vec.x;
            double y = vec.y;
            double z = vec.z;
            
            x /= 2.0D;
            z /= 2.0D;
            x -= d1 / (double)f * (double)power;
            z -= d0 / (double)f * (double)power;

			if (!orgEntity.isOnGround()) {
				y /= 2.0D;
				y += (double) power;

				if (y > 0.4000000059604645D) {
					y = 0.4000000059604645D;
				}
			}
            
            orgEntity.setMotion(x, y, z);
        }
	}
	
	public float getMaxStunArmor() {
		ModifiableAttributeInstance stun_resistance = this.orgEntity.getAttribute(ModAttributes.MAX_STUN_ARMOR.get());
		return (float) (stun_resistance == null ? 0 : stun_resistance.getValue());
	}
	
	public float getStunArmor() {
		return getMaxStunArmor() == 0 ? 0 : MathHelper.clamp(this.orgEntity.getDataManager().get(DataKeys.STUN_ARMOR).floatValue(), 0.0F, getMaxStunArmor());
	}
	
	public void setStunArmor(float value) {
		float f1 = Math.max(Math.min(value, this.getMaxStunArmor()), 0);
		this.orgEntity.getDataManager().set(DataKeys.STUN_ARMOR, f1);
	}
	
	public double getWeight() {
		return this.orgEntity.getAttributeValue(ModAttributes.WEIGHT.get());
	}
	
	public void rotateTo(float degree, float limit, boolean partialSync) {
		LivingEntity entity = this.getOriginalEntity();
		float amount = MathHelper.wrapDegrees(degree - entity.rotationYaw);
		
        while(amount < -180.0F) {
        	amount += 360.0F;
        }
        
        while(amount > 180.0F) {
        	amount -= 360.0F;
        }
        
        if (amount > limit) {
			amount = limit;
        }
        if (amount < -limit) {
        	amount = -limit;
        }
        
        float f1 = entity.rotationYaw + amount;
        
		if(partialSync) {
			entity.prevRotationYaw = f1;
			entity.prevRotationYawHead = f1;
			entity.prevRenderYawOffset = f1;
		}
		
		entity.rotationYaw = f1;
		entity.rotationYawHead = f1;
		entity.renderYawOffset = f1;
	}
	
	public void rotateTo(Entity target, float limit, boolean partialSync) {
		double d0 = target.getPosX() - this.orgEntity.getPosX();
        double d1 = target.getPosZ() - this.orgEntity.getPosZ();
        float degree = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
    	rotateTo(degree, limit, partialSync);
	}
	
	public void moveForward(float forward) {
		LivingEntity entity = this.getOriginalEntity();
		float x = -forward * MathHelper.sin(entity.rotationYaw * 0.017453292F);
        float z = forward * MathHelper.cos(entity.rotationYaw * 0.017453292F);
        entity.move(MoverType.SELF, new Vector3d(x, 0, z));
	}
	
	public void move(float horizontal, float vertical) {
		LivingEntity entity = this.getOriginalEntity();
		float x = -horizontal * MathHelper.sin(entity.rotationYaw * 0.017453292F);
        float z = horizontal * MathHelper.cos(entity.rotationYaw * 0.017453292F);
        entity.move(MoverType.SELF, new Vector3d(x, vertical, z));
	}
	
	public void playSound(SoundEvent sound, float minPitch, float maxPitch) {
		float randPitch = this.orgEntity.getRNG().nextFloat() * 2.0F - 1.0F;
		randPitch = Math.min(Math.max(randPitch, minPitch), maxPitch);
		if(!this.isRemote()) {
			this.orgEntity.world.playSound(null, orgEntity.getPosX(), orgEntity.getPosY(), orgEntity.getPosZ(), sound, orgEntity.getSoundCategory(), 1.0F, 1.0F + randPitch);
		} else {
			this.orgEntity.world.playSound(orgEntity.getPosX(), orgEntity.getPosY(), orgEntity.getPosZ(), sound, orgEntity.getSoundCategory(), 1.0F, 1.0F + randPitch, false);
		}
	}
	
	public LivingEntity getAttackTarget() {
		return this.orgEntity.getLastAttackedEntity();
	}
	
	public VisibleMatrix4f getHeadMatrix(float partialTicks) {
		float f;
        float f1;
        float f2;
		
		if (inaction) {
			f2 = 0;
		} else {
			f = MathUtils.interpolateRotation(orgEntity.prevRenderYawOffset, orgEntity.renderYawOffset, partialTicks);
			f1 = MathUtils.interpolateRotation(orgEntity.prevRotationYawHead, orgEntity.rotationYawHead, partialTicks);
			f2 = f1 - f;

			if (orgEntity.getRidingEntity() != null) {
				if (f2 > 45.0F) {
					f2 = 45.0F;
				} else if (f2 < -45.0F) {
					f2 = -45.0F;
				}
			}
		}
        
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.prevRotationPitch, orgEntity.rotationPitch, f2, f2, partialTicks, 1, 1, 1);
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		float prevRotYaw;
		float rotyaw;
		float scaleX = 1.0F;
		float scaleY = 1.0F;
		float scaleZ = 1.0F;
		
		if (orgEntity.getRidingEntity() instanceof LivingEntity) {
			LivingEntity ridingEntity = (LivingEntity) orgEntity.getRidingEntity();
			prevRotYaw = ridingEntity.prevRenderYawOffset;
			rotyaw = ridingEntity.renderYawOffset;
		} else {
			prevRotYaw = (inaction ? orgEntity.rotationYaw : orgEntity.prevRenderYawOffset);
			rotyaw = (inaction ? orgEntity.rotationYaw : orgEntity.renderYawOffset);
		}
		
		if (this.orgEntity.isChild()) {
			scaleX *= 0.5F;
			scaleY *= 0.5F;
			scaleZ *= 0.5F;
		}
		
		return MathUtils.getModelMatrixIntegrated((float)orgEntity.lastTickPosX, (float)orgEntity.getPosX(), (float)orgEntity.lastTickPosY, (float)orgEntity.getPosY(),
				(float)orgEntity.lastTickPosZ, (float)orgEntity.getPosZ(), 0, 0, prevRotYaw, rotyaw, partialTicks, scaleX, scaleY, scaleZ);
	}
	
	public void resetLivingMixLoop() {
		this.currentMixMotion = LivingMotion.NONE;
		this.getClientAnimator().resetMixMotion();
	}

	public void playAnimationSynchronize(int id, float modifyTime) {
		this.animator.playAnimation(id, modifyTime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}

	public void playAnimationSynchronize(StaticAnimation animation, float modifyTime) {
		this.playAnimationSynchronize(animation.getId(), modifyTime);
	}
	
	public void resetSize(EntitySize size) {
		EntitySize entitysize = this.orgEntity.size;
		EntitySize entitysize1 = size;
		this.orgEntity.size = entitysize1;
	    if (entitysize1.width < entitysize.width) {
	    	double d0 = (double)entitysize1.width / 2.0D;
	    	this.orgEntity.setBoundingBox(new AxisAlignedBB(orgEntity.getPosX() - d0, orgEntity.getPosY(), orgEntity.getPosZ() - d0, orgEntity.getPosX() + d0,
	    			orgEntity.getPosY() + (double)entitysize1.height, orgEntity.getPosZ() + d0));
	    } else {
	    	AxisAlignedBB axisalignedbb = this.orgEntity.getBoundingBox();
	    	this.orgEntity.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width,
	    			axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
	    	
	    	if (entitysize1.width > entitysize.width && !orgEntity.world.isRemote) {
	    		float f = entitysize.width - entitysize1.width;
	        	this.orgEntity.move(MoverType.SELF, new Vector3d((double)f, 0.0D, (double)f));
	    	}
	    }
    }
	
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		
	}
	
	@SuppressWarnings("unchecked")
	public <A extends Animator> A getAnimator() {
		return (A) this.animator;
	}

	public AnimatorClient getClientAnimator() {
		return this.<AnimatorClient>getAnimator();
	}

	public AnimatorServer getServerAnimator() {
		return this.<AnimatorServer>getAnimator();
	}

	public abstract StaticAnimation getHitAnimation(StunType stunType);

	@Override
	public void aboutToDeath() {
		this.animator.onEntityDeath();
	}

	@Override
	public T getOriginalEntity() {
		return orgEntity;
	}

	public SoundEvent getWeaponHitSound(Hand hand) {
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null)
			return cap.getHitSound();

		return Sounds.BLUNT_HIT;
	}

	public SoundEvent getSwingSound(Hand hand) {
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null) {
			return cap.getSmashingSound();
		}

		return Sounds.WHOOSH;
	}
	
	public HitParticleType getWeaponHitParticle(Hand hand) {
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null)
			return cap.getHitParticle();

		return Particles.HIT_BLUNT.get();
	}

	public Collider getColliderMatching(Hand hand) {
		CapabilityItem itemCap = this.getHeldItemCapability(hand);
		return itemCap != null ? itemCap.getWeaponCollider() : Colliders.fist;
	}

	public int getHitEnemies() {
		return (int) this.orgEntity.getAttributeValue(ModAttributes.HIT_AT_ONCE.get());
	}

	public float getDefenceIgnore() {
		return (float) this.orgEntity.getAttributeValue(ModAttributes.IGNORE_DEFENCE.get());
	}

	public float getImpact() {
		return (float) this.orgEntity.getAttributeValue(ModAttributes.IMPACT.get());
	}

	public boolean isTeam(Entity entityIn) {
		if (orgEntity.getRidingEntity() != null && orgEntity.getRidingEntity().equals(entityIn))
			return true;
		else if (this.isMountedTeam(entityIn))
			return true;

		return this.orgEntity.isOnSameTeam(entityIn);
	}

	private boolean isMountedTeam(Entity entityIn) {
		LivingEntity orgEntity = this.getOriginalEntity();
		for (Entity passanger : orgEntity.getPassengers()) {
			if (passanger.equals(entityIn))
				return true;
		}

		for (Entity passanger : entityIn.getPassengers()) {
			if (passanger.equals(orgEntity))
				return true;
		}

		return false;
	}
	
	public boolean isFirstPerson() {
		return false;
	}
	
	public EntityState getEntityState() {
		return this.animator.getPlayer().getPlay().getState(animator.getPlayer().getElapsedTime());
	}

	public static enum EntityState {
		FREE(false, false, false, false, true, 0),
		FREE_CAMERA(false, true, false, false, false, 1),
		FREE_INPUT(false, false, false, false, true, 3),
		PRE_DELAY(true, true, false, false, false, 1),
		CONTACT(true, true, true, false, false, 2),
		ROTATABLE_CONTACT(false, true, true, false, false, 2),
		POST_DELAY(true, true, false, false, true, 3),
		ROTATABLE_POST_DELAY(false, true, false, false, true, 3),
		HIT(true, true, false, false, false, 3),
		DODGE(true, true, false, true, false, 3);
		
		boolean cameraLock;
		boolean movementLock;
		boolean collideDetection;
		boolean invincible;
		boolean canAct;
		// none : 0, beforeContact : 1, contact : 2, afterContact : 3
		int level;
		
		EntityState(boolean cameraLock, boolean movementLock, boolean collideDetection, boolean invincible, boolean canAct, int level) {
			this.cameraLock = cameraLock;
			this.movementLock = movementLock;
			this.collideDetection = collideDetection;
			this.invincible = invincible;
			this.canAct = canAct;
			this.level = level;
		}
		
		public boolean isCameraRotationLocked() {
			return this.cameraLock;
		}
		
		public boolean isMovementLocked() {
			return this.movementLock;
		}
		
		public boolean shouldDetectCollision() {
			return this.collideDetection;
		}
		
		public boolean isInvincible() {
			return this.invincible;
		}
		
		public boolean canAct() {
			return this.canAct;
		}
		
		public int getLevel() {
			return this.level;
		}
	}
}