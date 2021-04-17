package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.entity.DataKeys;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;

public class WitherSkeletonData extends SkeletonData<WitherSkeletonEntity> {
	public WitherSkeletonData() {
		super(Faction.WITHER_ARMY);
	}
	
	@Override
	public void onEntityJoinWorld(WitherSkeletonEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getDataManager().set(DataKeys.STUN_ARMOR, 4.0F);
	}
	
	@Override
	public void postInit() {
		super.resetCombatAI();
		super.postInit();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.MAX_STUN_ARMOR.get()).setBaseValue(4.0F);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animator) {
		animator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.WITHER_SKELETON_IDLE);
		animator.addLivingAnimation(LivingMotion.WALKING, Animations.WITHER_SKELETON_WALK);
		animator.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public boolean hurtEntity(Entity hitTarget, Hand handIn, IExtendedDamageSource source, float amount) {
		boolean succed = super.hurtEntity(hitTarget, handIn, source, amount);
		if (succed && hitTarget instanceof LivingEntity && this.orgEntity.getRNG().nextInt(10) == 0) {
            ((LivingEntity)hitTarget).addPotionEffect(new EffectInstance(Effects.WITHER, 200));
        }
		
		return succed;
	}
	
	@Override
	public void setAIAsArmed() {
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.5D, true, MobAttackPatterns.WITHER_SKELETON_PATTERN));
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.2D, true, Animations.WITHER_SKELETON_CHASE, Animations.WITHER_SKELETON_WALK));
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);
		return VisibleMatrix4f.scale(new Vec3f(1.2F, 1.2F, 1.2F), mat, mat);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_SKELETON;
	}
}