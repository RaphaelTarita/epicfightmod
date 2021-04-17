package maninhouse.epicfight.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.Quaternion;
import maninhouse.epicfight.animation.property.Property.AnimationProperty;
import maninhouse.epicfight.animation.property.Property.DamageProperty;
import maninhouse.epicfight.animation.types.ActionAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.MobData;
import maninhouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.event.EntityEventListener.EventType;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.game.AttackResult;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.DamageType;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.Vec3f;
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
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.RegistryObject;

public class AttackAnimation extends ActionAnimation {
	protected final Map<AnimationProperty<?>, Object> properties;
	public final Phase[] phases;
	
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
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		
		if (!entitydata.isRemote()) {
			float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
			float prevElapsedTime = entitydata.getAnimator().getPlayer().getPrevElapsedTime();
			LivingData.EntityState state = this.getState(elapsedTime);
			LivingData.EntityState prevState = this.getState(prevElapsedTime);
			Phase phase = this.getPhaseByTime(elapsedTime);
			if(state == LivingData.EntityState.FREE_CAMERA) {
				if(entitydata instanceof MobData) {
					((MobEntity) entitydata.getOriginalEntity()).getNavigator().clearPath();
					LivingEntity target = entitydata.getAttackTarget();
					if(target != null) {
						entitydata.rotateTo(target, 60.0F, false);
					}
				}
			} else if (state.shouldDetectCollision() || (prevState.getLevel() < 2 && state.getLevel() > 2)) {
				if(!prevState.shouldDetectCollision()) {
					entitydata.playSound(this.getSwingSound(entitydata, phase), 0.0F, 0.0F);
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
					boolean flag1 = true;
					int maxStrikes = this.getMaxStrikes(entitydata, phase);
					while (entitydata.currentlyAttackedEntity.size() < maxStrikes) {
						Entity e = attackResult.getEntity();
						Entity trueEntity = this.getTrueEntity(e);
						if (!entitydata.currentlyAttackedEntity.contains(trueEntity) && !entitydata.isTeam(e)) {
							if (e instanceof LivingEntity || e instanceof PartEntity) {
								if(entity.world.rayTraceBlocks(new RayTraceContext(new Vector3d(e.getPosX(), e.getPosY() + (double)e.getEyeHeight(), e.getPosZ()),
										new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() * 0.5F, entity.getPosZ()), 
										RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS) {
									
									IExtendedDamageSource source = this.getDamageSourceExt(entitydata, e, phase);
									if(entitydata.hurtEntity(e, phase.hand, source, this.getDamageAmount(entitydata, e, phase))) {
										e.hurtResistantTime = 0;
										e.world.playSound(null, e.getPosX(), e.getPosY(), e.getPosZ(), this.getHitSound(entitydata, phase), e.getSoundCategory(), 1.0F, 1.0F);
										this.spawnHitParticle(((ServerWorld)e.world), entitydata, e, phase);
										if(flag1 && entitydata instanceof PlayerData && trueEntity instanceof LivingEntity) {
											entitydata.getOriginalEntity().getHeldItem(phase.hand).hitEntity((LivingEntity)trueEntity, ((PlayerData<?>)entitydata).getOriginalEntity());
											flag1 = false;
										}
									}
									entitydata.currentlyAttackedEntity.add(trueEntity);
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
		
		if(entitydata instanceof PlayerData) {
			((PlayerData<?>)entitydata).getEventListener().activateEvents(EventType.ON_ATTACK_END_EVENT, entitydata.currentlyAttackedEntity.size(), this.getId());
		}
		
		entitydata.currentlyAttackedEntity.clear();
		
		if(entitydata instanceof BipedMobData && entitydata.isRemote()) {
			MobEntity entity = (MobEntity) entitydata.getOriginalEntity();
			if(entity.getAttackTarget() !=null && !entity.getAttackTarget().isAlive())
				entity.setAttackTarget((LivingEntity)null);
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time) {
		Phase phase = this.getPhaseByTime(time);
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
		Phase phase = this.getPhaseByTime(elapsedTime);
		return phase.collider != null ? phase.collider : entitydata.getColliderMatching(phase.hand);
	}
	
	public Entity getTrueEntity(Entity entity) {
		if (entity instanceof PartEntity) {
			return ((PartEntity<?>)entity).getParent();
		}
		
		return entity;
	}
	
	protected int getMaxStrikes(LivingData<?> entitydata, Phase phase) {
		int i = entitydata.getHitEnemies();
		phase.getProperty(DamageProperty.MAX_STRIKES).ifPresent((opt)->opt.get(i));
		
		return i;
	}
	
	protected float getDamageAmount(LivingData<?> entitydata, Entity target, Phase phase) {
		float f = entitydata.getDamageToEntity(target, phase.hand);
		phase.getProperty(DamageProperty.DAMAGE).ifPresent((opt)->opt.get(f));
		return f;
	}
	
	protected SoundEvent getSwingSound(LivingData<?> entitydata, Phase phase) {
		return phase.getProperty(DamageProperty.SWING_SOUND).orElse(entitydata.getSwingSound(phase.hand));
	}
	
	protected SoundEvent getHitSound(LivingData<?> entitydata, Phase phase) {
		return phase.getProperty(DamageProperty.HIT_SOUND).orElse(entitydata.getWeaponHitSound(phase.hand));
	}
	
	protected IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target, Phase phase) {
		DamageType dmgType = phase.getProperty(DamageProperty.DAMAGE_TYPE).orElse(DamageType.PHYSICAL);
		StunType stunType = phase.getProperty(DamageProperty.STUN_TYPE).orElse(StunType.SHORT);
		IExtendedDamageSource extDmgSource = entitydata.getDamageSource(stunType, dmgType, this.getId());
		
		phase.getProperty(DamageProperty.ARMOR_NEGATION).ifPresent((opt) -> {
			extDmgSource.setArmorNegation(opt.get(extDmgSource.getArmorNegation()));
		});
		phase.getProperty(DamageProperty.IMPACT).ifPresent((opt) -> {
			extDmgSource.setImpact(opt.get(extDmgSource.getImpact()));
		});
		
		return extDmgSource;
	}
	
	protected void spawnHitParticle(ServerWorld world, LivingData<?> attacker, Entity hit, Phase phase) {
		Optional<RegistryObject<HitParticleType>> particleOptional = phase.getProperty(DamageProperty.PARTICLE);
		HitParticleType particle;
		
		if(particleOptional.isPresent()) {
			particle = particleOptional.get().get();
		} else {
			particle = attacker.getWeaponHitParticle(phase.hand);
		}
		
		particle.spawnParticleWithArgument(world, HitParticleType.DEFAULT, hit, attacker.getOriginalEntity());
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		Pose pose = super.getPoseByTime(entitydata, time);
		
		this.getProperty(AnimationProperty.DIRECTIONAL).ifPresent((b)->{
			float pitch = entitydata.getAttackDirectionPitch();
			JointTransform chest = pose.getTransformByName("Chest");
			chest.setCustomRotation(Quaternion.rotate((float)Math.toRadians(pitch), new Vec3f(1,0,0), null));
			
			if (entitydata instanceof PlayerData) {
				JointTransform head = pose.getTransformByName("Head");
				head.setRotation(Quaternion.rotate((float)-Math.toRadians(pitch), new Vec3f(1,0,0), head.getRotation()));
			}
		});
		
		return pose;
	}
	
	public <V> AttackAnimation addProperty(AnimationProperty<V> propertyType, V value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	public <V> AttackAnimation addProperty(DamageProperty<V> propertyType, V value) {
		return this.addProperty(propertyType, value, 0);
	}
	
	public <V> AttackAnimation addProperty(DamageProperty<V> propertyType, V value, int index) {
		this.phases[index].addProperty(propertyType, value);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	protected <V> Optional<V> getProperty(AnimationProperty<V> propertyType) {
		return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
	}
	
	public int getIndexer(float elapsedTime) {
		return this.getPhaseByTime(elapsedTime).jointIndexer;
	}
	
	public Phase getPhaseByTime(float elapsedTime) {
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
		protected final Map<DamageProperty<?>, Object> properties = new HashMap<DamageProperty<?>, Object> ();;
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
		
		public <V> Phase addProperty(DamageProperty<V> propertyType, V value) {
			this.properties.put(propertyType, value);
			return this;
		}
		
		public void addProperties(Set<Map.Entry<DamageProperty<?>, Object>> set) {
			for(Map.Entry<DamageProperty<?>, Object> entry : set) {
				this.properties.put(entry.getKey(), entry.getValue());
			}
		}
		
		@SuppressWarnings("unchecked")
		protected <V> Optional<V> getProperty(DamageProperty<V> propertyType) {
			return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
		}
	}
}