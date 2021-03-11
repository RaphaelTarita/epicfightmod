package maninhouse.epicfight.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import maninhouse.epicfight.animation.types.ActionAnimation;
import maninhouse.epicfight.animation.types.AnimationProperty;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.MobData;
import maninhouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.game.AttackResult;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.DamageType;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

public class AttackAnimation extends ActionAnimation {
	protected final Map<AnimationProperty<?>, Object> properties;
	protected final Phase[] phases;
	
	public AttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider,
			String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public AttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand, @Nullable Collider collider,
			String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public AttackAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases) {
		super(id, convertTime, true, affectY, path);
		this.properties = new HashMap<AnimationProperty<?>, Object> ();
		this.phases = phases;
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata)
	{
		super.onUpdate(entitydata);
		
		if(!entitydata.isRemote()) {
			float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
			LivingData.EntityState state = this.getState(elapsedTime);
			Phase phase = this.getCurrentPhase(elapsedTime);
			if(state == LivingData.EntityState.FREE_CAMERA) {
				if(entitydata instanceof MobData) {
					((MobEntity) entitydata.getOriginalEntity()).getNavigator().clearPath();
					LivingEntity target = entitydata.getAttackTarget();
					if(target != null) {
						entitydata.rotateTo(target, 60.0F, false);
					}
				}
			} else if(state.shouldDetectCollision()) {
				if(!this.getState(entitydata.getAnimator().getPlayer().getPrevElapsedTime()).shouldDetectCollision()) {
					entitydata.playSound(this.getSwingSound(entitydata, phase.hand), 0.0F, 0.0F);
					entitydata.currentlyAttackedEntity.clear();
				}
				
				Collider collider = this.getCollider(entitydata, elapsedTime);
				LivingEntity entity = entitydata.getOriginalEntity();
				entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature().initializeTransform();
				VisibleMatrix4f jointTransform = entitydata.getServerAnimator().getColliderTransformMatrix(phase.jointIndexer);
				collider.transform(VisibleMatrix4f.mul(entitydata.getModelMatrix(1.0F), jointTransform, null));
				List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, collider.getHitboxAABB());
				collider.extractHitEntities(list);
				
				if (list.size() > 0) {
					AttackResult attackResult = new AttackResult(entity, list);
					int i = 0;
					while (entitydata.currentlyAttackedEntity.size() < getHitEnemies(entitydata)) {
						Entity e = attackResult.getEntity();
						if (!entitydata.currentlyAttackedEntity.contains(e) && !entitydata.isTeam(e)) {
							if (e instanceof LivingEntity) {
								if(entity.world.rayTraceBlocks(new RayTraceContext(new Vector3d(e.getPosX(), e.getPosY() + (double)e.getEyeHeight(), e.getPosZ()),
										new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() * 0.5F, entity.getPosZ()), 
										RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS) {
									IExtendedDamageSource source = this.getDamageSourceExt(entitydata, e);
									if(entitydata.hurtEntity(e, source, this.getDamageAmount(entitydata, e, phase.hand))) {
										entity.setLastAttackedEntity(e);
										e.hurtResistantTime = 0;
										e.world.playSound(null, e.getPosX(), e.getPosY(), e.getPosZ(), this.getHitSound(entitydata, phase.hand), e.getSoundCategory(), 1.0F, 1.0F);
										this.spawnHitParticle(((ServerWorld)e.world), entitydata, e, phase.hand);
										if(i == 0 && entitydata instanceof PlayerData)
											entitydata.getOriginalEntity().getHeldItem(phase.hand).hitEntity((LivingEntity)e, ((PlayerData<?>)entitydata).getOriginalEntity());
										i++;
									}
									entitydata.currentlyAttackedEntity.add(e);
								}
							}
						}
						
						if(!attackResult.next()) {
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		super.onFinish(entitydata, isEnd);
		entitydata.currentlyAttackedEntity.clear();
		
		if(entitydata instanceof BipedMobData && entitydata.isRemote()) {
			MobEntity entity = (MobEntity) entitydata.getOriginalEntity();
			if(entity.getAttackTarget() !=null && !entity.getAttackTarget().isAlive())
				entity.setAttackTarget((LivingEntity)null);
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time) {
		Phase phase = this.getCurrentPhase(time);
		boolean lockCameraRotation = this.getProperty(AnimationProperty.LOCK_ROTATION).orElse(false);
		
		if(phase.antic >= time)
			return LivingData.EntityState.FREE_CAMERA;
		else if(phase.antic < time && phase.preDelay > time)
			return LivingData.EntityState.FREE_CAMERA;
		else if(phase.preDelay <= time && phase.contact >= time)
			return lockCameraRotation ? LivingData.EntityState.CONTACT : LivingData.EntityState.ROTATABLE_CONTACT;
		else if(phase.recovery > time)
			return lockCameraRotation ? LivingData.EntityState.POST_DELAY : LivingData.EntityState.ROTATABLE_POST_DELAY;
		else
			return LivingData.EntityState.FREE_INPUT;
	}
	
	public Collider getCollider(LivingData<?> entitydata, float elapsedTime) {
		Phase phase = this.getCurrentPhase(elapsedTime);
		return phase.collider != null ? phase.collider : entitydata.getColliderMatching(phase.hand);
	}
	
	protected int getHitEnemies(LivingData<?> entitydata) {
		return this.getProperty(AnimationProperty.HIT_AT_ONCE).orElse(entitydata.getHitEnemies());
	}
	
	protected float getDamageAmount(LivingData<?> entitydata, Entity target, Hand hand) {
		float multiplier = this.getProperty(AnimationProperty.DAMAGE_MULTIPLIER).orElse(1.0F);
		float adder = this.getProperty(AnimationProperty.DAMAGE_ADDER).orElse(0.0F);
		return entitydata.getDamageToEntity(target, hand) * multiplier + adder;
	}
	
	protected SoundEvent getSwingSound(LivingData<?> entitydata, Hand hand) {
		return this.getProperty(AnimationProperty.SWING_SOUND).orElse(entitydata.getSwingSound(hand));
	}
	
	protected SoundEvent getHitSound(LivingData<?> entitydata, Hand hand) {
		return this.getProperty(AnimationProperty.HIT_SOUND).orElse(entitydata.getWeaponHitSound(hand));
	}
	
	protected IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target) {
		DamageType dmgType = this.getProperty(AnimationProperty.DAMAGE_TYPE).orElse(DamageType.PHYSICAL);
		StunType stunType = this.getProperty(AnimationProperty.STUN_TYPE).orElse(StunType.SHORT);
		IExtendedDamageSource extDmgSource = entitydata.getDamageSource(stunType, dmgType, this.getId());
		
		this.getProperty(AnimationProperty.ARMOR_NEGATION).ifPresent((opt) -> {
			extDmgSource.setArmorIgnore(opt);
		});
		this.getProperty(AnimationProperty.IMPACT).ifPresent((opt) -> {
			extDmgSource.setImpact(opt);
		});
		
		return extDmgSource;
	}
	
	protected void spawnHitParticle(ServerWorld world, LivingData<?> attacker, Entity hit, Hand hand) {
		Optional<RegistryObject<HitParticleType>> particleOptional = this.getProperty(AnimationProperty.PARTICLE);
		HitParticleType particle;
		
		if(particleOptional.isPresent())
			particle = particleOptional.get().get();
		else
			particle = attacker.getWeaponHitParticle(hand);
		
		particle.spawnParticleWithArgument(world, HitParticleType.DEFAULT, hit, attacker.getOriginalEntity());
	}
	
	public <T> AttackAnimation addProperty(AnimationProperty<T> propertyType, T value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	public void addProperties(Set<Map.Entry<AnimationProperty<?>, Object>> set) {
		for(Map.Entry<AnimationProperty<?>, Object> entry : set) {
			this.properties.put(entry.getKey(), entry.getValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> Optional<T> getProperty(AnimationProperty<T> propertyType) {
		return (Optional<T>) Optional.ofNullable(this.properties.get(propertyType));
	}
	
	public int getIndexer(float elapsedTime) {
		return this.getCurrentPhase(elapsedTime).jointIndexer;
	}
	
	public Phase getCurrentPhase(float elapsedTime) {
		Phase currentPhase = null;
		for(Phase phase : this.phases) {
			currentPhase = phase;
			if(phase.recovery > elapsedTime) {
				break;
			}
		}
		return currentPhase;
	}
	
	@Deprecated
	public void changeCollider(Collider newCollider, int index) {
		this.phases[index].collider = newCollider;
	}
	
	public static class Phase {
		protected final float antic;
		protected final float preDelay;
		protected final float contact;
		protected final float recovery;
		protected final int jointIndexer;
		protected final Hand hand;
		protected Collider collider;
		
		public Phase(float antic, float preDelay, float contact, float recovery, String indexer, Collider collider) {
			this(antic, preDelay, contact, recovery, Hand.MAIN_HAND, indexer, collider);
		}
		
		public Phase(float antic, float preDelay, float contact, float recovery, Hand hand, String indexer, Collider collider) {
			this.antic = antic;
			this.preDelay = preDelay;
			this.contact = contact;
			this.recovery = recovery;
			this.collider = collider;
			this.hand = hand;
			
			int coded = 0;
			if (indexer.length() == 0) {
				this.jointIndexer = -1;
			} else {
				for (int i = 0; i < indexer.length(); i++) {
					int value = indexer.charAt(i) - '0';
					coded = coded | value;
					coded = coded << 5;
				}
				this.jointIndexer = coded;
			}
		}
	}
}