package maninhouse.epicfight.capabilities.entity.mob;

import java.util.EnumSet;
import java.util.Random;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.DataKeys;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.effects.ModEffects;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.AttackPatternPercentGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimationTP;
import maninhouse.epicfight.network.server.STCPlayAnimationTarget;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class EndermanData extends BipedMobData<EndermanEntity> {
	private int deathTimerExt = 0;
	private boolean onRage;
	private Goal normalAttack1;
	private Goal normalAttack2;
	private Goal normalAttack3;
	private Goal normalAttack4;
	private Goal normalAttack5;
	private Goal rageTarget;
	private Goal rageChase;
	
	public EndermanData() {
		super(Faction.ENDERLAND);
	}
	
	@Override
	public boolean onEntityJoinWorld(EndermanEntity entityIn) {
		if(super.onEntityJoinWorld(entityIn)) {
			this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(6.0F));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.MAX_STUN_ARMOR.get()).setBaseValue(6.0F);
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.8F);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		if (this.isRemote()) {
			if (this.isRaging()) {
				this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_RAGE_IDLE);
				this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_RAGE_WALK);
				this.onRage = true;
			} else {
				this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
				this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_WALK);
				this.onRage = false;
			}
		}
	}
	
	@Override
	protected void initAI() {
		super.initAI();
    	normalAttack1 = new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 1.23D, 0.4F, true, MobAttackPatterns.ENDERMAN_PATTERN1);
    	normalAttack2 = new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 1.9D, 0.4F, true, MobAttackPatterns.ENDERMAN_PATTERN2);
    	normalAttack3 = new AttackPatternPercentGoal(this, this.orgEntity, 3.0D, 4.0D, 0.1F, true, MobAttackPatterns.ENDERMAN_PATTERN3);
    	normalAttack4 = new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 2.0D, 0.2F, true, MobAttackPatterns.ENDERMAN_PATTERN4);
    	normalAttack5 = new AIEndermanTeleportKick(this, this.orgEntity);
    	rageTarget = new NearestAttackableTargetGoal<>(this.orgEntity, PlayerEntity.class, true);
    	rageChase = new AIEndermanRush(this, this.orgEntity);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.ENDERMAN_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void updateOnServer() {
		super.updateOnServer();
		if (this.isRaging() && !this.onRage && this.orgEntity.ticksExisted > 5) {
			this.convertRage();
		} else if (this.onRage && !this.isRaging()) {
			this.convertNormal();
		}
	}
	
	@Override
	public void update() {
		if (orgEntity.getHealth() <= 0.0F) {
			orgEntity.rotationPitch = 0;
			if (orgEntity.deathTime > 1 && this.deathTimerExt < 20) {
				deathTimerExt++;
				orgEntity.deathTime--;
			}
		}
		super.update();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if (damageSource instanceof EntityDamageSource && !this.isRaging()) {
			IExtendedDamageSource extDamageSource = null;
			if(damageSource instanceof IExtendedDamageSource) {
				extDamageSource = ((IExtendedDamageSource)damageSource);
			}
			
			if (extDamageSource == null || extDamageSource.getStunType() != StunType.HOLD) {
				int percentage = this.animator.getPlayer().getPlay() instanceof AttackAnimation ? 10 : 3;
				if (orgEntity.getRNG().nextInt(percentage) == 0) {
					for (int i = 0; i < 9; i++) {
						if (this.teleportRandomly()) {
							if(damageSource.getTrueSource() instanceof LivingEntity) {
								this.orgEntity.setRevengeTarget((LivingEntity) damageSource.getTrueSource());
							}
							
							if(this.inaction) {
								this.playAnimationSynchronize(Animations.ENDERMAN_TP_EMERGENCE, 0.0F);
							}
							
							return false;
						}
					}
				}
			}
		}
		
		return super.attackEntityFrom(damageSource, amount);
	}
	
	protected boolean teleportRandomly() {
		if (!this.isRemote() && this.orgEntity.isAlive()) {
	        double d0 = this.orgEntity.getPosX() + (this.orgEntity.getRNG().nextDouble() - 0.5D) * 64.0D;
	        double d1 = this.orgEntity.getPosY() + (double)(this.orgEntity.getRNG().nextInt(64) - 32);
	        double d2 = this.orgEntity.getPosZ() + (this.orgEntity.getRNG().nextDouble() - 0.5D) * 64.0D;
	        return this.teleportTo(d0, d1, d2);
		} else {
			return false;
		}
    }
	
	private boolean teleportTo(double x, double y, double z) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);
	    while(blockpos$mutable.getY() > 0 && !this.orgEntity.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement())
	    	blockpos$mutable.move(Direction.DOWN);
	    
	    BlockState blockstate = this.orgEntity.world.getBlockState(blockpos$mutable);
	    boolean flag = blockstate.getMaterial().blocksMovement();
	    boolean flag1 = blockstate.getFluidState().isTagged(FluidTags.WATER);
		if (flag && !flag1) {
	    	boolean flag2 = this.orgEntity.attemptTeleport(x, y, z, true);
			if (flag2 && !this.orgEntity.isSilent()) {
	        	this.orgEntity.world.playSound((PlayerEntity)null, this.orgEntity.prevPosX, this.orgEntity.prevPosY, this.orgEntity.prevPosZ,
	        			SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.orgEntity.getSoundCategory(), 1.0F, 1.0F);
	        	this.orgEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
	        }
	        return flag2;
	    }
        return false;
    }
	
	public boolean isRaging() {
		return this.orgEntity.getHealth() / this.orgEntity.getMaxHealth() < 0.33F;
	}
	
	protected void convertRage() {
		this.onRage = true;
		this.playAnimationSynchronize(Animations.ENDERMAN_HIT_RAGE, 0);
		
		if (this.isRemote()) {
			this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_RAGE_IDLE);
			this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_RAGE_WALK);
		} else {
			if (!orgEntity.isAIDisabled()) {
				this.orgEntity.goalSelector.removeGoal(normalAttack1);
				this.orgEntity.goalSelector.removeGoal(normalAttack2);
				this.orgEntity.goalSelector.removeGoal(normalAttack3);
				this.orgEntity.goalSelector.removeGoal(normalAttack4);
				this.orgEntity.goalSelector.removeGoal(normalAttack5);
				this.orgEntity.goalSelector.addGoal(1, rageChase);
				this.orgEntity.targetSelector.addGoal(3, rageTarget);
				this.orgEntity.getDataManager().set(EndermanEntity.SCREAMING, Boolean.valueOf(true));
				this.orgEntity.addPotionEffect(new EffectInstance(ModEffects.STUN_IMMUNITY, 120000));
			}
		}
	}
	
	protected void convertNormal()
	{
		this.onRage = false;
		
		if(this.isRemote())
		{
			this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
			this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_WALK);
		}
		else
		{
			if(!orgEntity.isAIDisabled())
			{
				this.orgEntity.goalSelector.addGoal(1, normalAttack1);
				this.orgEntity.goalSelector.addGoal(1, normalAttack2);
				this.orgEntity.goalSelector.addGoal(1, normalAttack3);
				this.orgEntity.goalSelector.addGoal(1, normalAttack4);
				this.orgEntity.goalSelector.addGoal(0, normalAttack5);
				this.orgEntity.goalSelector.removeGoal(rageChase);
				this.orgEntity.targetSelector.removeGoal(rageTarget);
				
				if(this.orgEntity.getAttackTarget() == null)
				{
					this.orgEntity.getDataManager().set(EndermanEntity.SCREAMING, Boolean.valueOf(false));
				}
				this.orgEntity.removePotionEffect(ModEffects.STUN_IMMUNITY);
			}
		}
	}
	
	@Override
	public void setAIAsUnarmed()
	{
		if(this.isRaging())
		{
			orgEntity.targetSelector.addGoal(3, rageTarget);
			orgEntity.goalSelector.addGoal(1, rageChase);
		}
		else
		{
			orgEntity.goalSelector.addGoal(1, normalAttack1);
			orgEntity.goalSelector.addGoal(1, normalAttack2);
			orgEntity.goalSelector.addGoal(1, normalAttack3);
			orgEntity.goalSelector.addGoal(1, normalAttack4);
			orgEntity.goalSelector.addGoal(0, normalAttack5);
		}
		
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 0.75D, false));
	}
	
	@Override
	public void setAIAsArmed()
	{
		this.setAIAsUnarmed();
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity)
	{
		
	}
	
	@Override
	public void aboutToDeath()
	{
		orgEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		
		if(this.isRemote())
		{
			for(int i = 0; i < 100; i++)
			{
				Random rand = orgEntity.getRNG();
				Vec3f vec = new Vec3f(rand.nextInt(), rand.nextInt(), rand.nextInt());
				vec.normalise();
				vec.scale(0.5F);
				Minecraft.getInstance().particles.addParticle(Particles.PORTAL_STRAIGHT.get(), orgEntity.getPosX(),
						orgEntity.getPosY() + orgEntity.getSize(Pose.STANDING).height / 2, orgEntity.getPosZ(), vec.x, vec.y, vec.z);
			}
		}
		
		super.aboutToDeath();
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType)
	{
		if(stunType == StunType.LONG)
		{
			return Animations.ENDERMAN_HIT_LONG;
		}
		else
		{
			return Animations.ENDERMAN_HIT_SHORT;
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB)
	{
		return modelDB.ENTITY_ENDERMAN;
	}
	
	static class AIEndermanTeleportKick extends AttackPatternPercentGoal
	{
		private int delayCounter;
		private int cooldownTime;
		
		public AIEndermanTeleportKick(BipedMobData<?> mobdata, MobEntity attacker)
		{
			super(mobdata, attacker, 8.0F, 100.0F, 0.1F, false, null);
			super.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}
		
		@Override
	    public boolean shouldExecute()
	    {
			boolean b = cooldownTime <= 0;
			if(!b) cooldownTime--;
			return super.shouldExecute() && b;
	    }
		
		@Override
	    public boolean shouldContinueExecuting()
	    {
			LivingEntity LivingEntity = this.attacker.getAttackTarget();
			boolean b = cooldownTime <= 100;
			if(!b) cooldownTime = 500;
	    	return isValidTarget(LivingEntity) && isTargetInRange(LivingEntity) && b;
	    }
		
		@Override
		public void startExecuting()
		{
			delayCounter = 35 + attacker.getRNG().nextInt(10);
			cooldownTime = 0;
		}
		
		@Override
	    public void resetTask()
	    {
			;
	    }
		
		@Override
	    public void tick()
	    {
			LivingEntity target = attacker.getAttackTarget();
	        this.attacker.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
	        
	        if(delayCounter-- < 0 && !this.mobdata.isInaction())
	        {
	        	Vec3f vec = new Vec3f((float)(attacker.getPosX() - target.getPosX()), 0, (float)(attacker.getPosZ() - target.getPosZ()));
	        	vec.normalise();
	        	vec.scale(1.414F);
	        	
	        	boolean flag = this.attacker.attemptTeleport(target.getPosX() + vec.x, target.getPosY(), target.getPosZ() + vec.z, true);
	        	
	            if (flag)
	            {
	            	this.mobdata.rotateTo(target, 360.0F, true);
	            	
	                AttackAnimation kickAnimation = attacker.getRNG().nextBoolean() ? (AttackAnimation) Animations.ENDERMAN_TP_KICK1 : (AttackAnimation) Animations.ENDERMAN_TP_KICK2;
		        	mobdata.getServerAnimator().playAnimation(kickAnimation, 0);
		        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTP(kickAnimation.getId(), attacker.getEntityId(), 0.0F, 
		        			attacker.getAttackTarget().getEntityId(), attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), attacker.rotationYaw), attacker);
		        	
		        	attacker.world.playSound((PlayerEntity)null, attacker.prevPosX, attacker.prevPosY, attacker.prevPosZ,
		        			SoundEvents.ENTITY_ENDERMAN_TELEPORT, attacker.getSoundCategory(), 1.0F, 1.0F);
	                attacker.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
	                cooldownTime = 0;
	            }
	            else
	            	cooldownTime++;
	        }
	    }
	}
	
	static class AIEndermanRush extends AttackPatternGoal
	{
		private float accelator;
		
		public AIEndermanRush(BipedMobData<?> mobdata, MobEntity attacker)
		{
			super(mobdata, attacker, 0.0F, 1.8F, false, null);
		}
		
		@Override
	    public boolean shouldExecute()
	    {
			return this.isValidTarget(attacker.getAttackTarget()) && !this.mobdata.isInaction();
	    }
		
		@Override
	    public boolean shouldContinueExecuting()
	    {
	    	return isValidTarget(attacker.getAttackTarget()) && !this.mobdata.isInaction();
	    }
		
		@Override
		public void startExecuting()
		{
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(
					403, attacker.getEntityId(), -1, true, attacker.getAttackTarget().getEntityId()), attacker);
			
			this.accelator = 0.0F;
		}
		
		@Override
	    public void resetTask()
	    {
			;
	    }
		
		@Override
	    public void tick()
	    {
			if(isTargetInRange(attacker.getAttackTarget()) && canExecuteAttack())
			{
	        	mobdata.getServerAnimator().playAnimation(Animations.ENDERMAN_GRASP, 0);
	        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(Animations.ENDERMAN_GRASP.getId(), attacker.getEntityId(), 0, 
	        			attacker.getAttackTarget().getEntityId()), attacker);
			}
			
			attacker.getNavigator().setSpeed(0.025F * accelator * accelator + 1.0F);
			accelator = accelator > 2.0F ? accelator : accelator + 0.05F;
	    }
	}
}