package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.entity.DataKeys;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.entity.ai.brain.BrainRemodeler;
import maninhouse.epicfight.entity.ai.brain.task.AttackWithPatternTask;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;

public class PiglinBruteData extends BipedMobData<PiglinBruteEntity> {
	public PiglinBruteData() {
		super(Faction.PIGLIN_ARMY);
	}
	
	@Override
	public boolean onEntityJoinWorld(PiglinBruteEntity entityIn) {
		if(super.onEntityJoinWorld(entityIn)) {
			this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(8.0F));
			BrainRemodeler.replaceTask(this.orgEntity.getBrain(), Activity.FIGHT, 12, AttackTargetTask.class, new AttackWithPatternTask(this, MobAttackPatterns.BIPED_ARMED_ONEHAND, 0.0D, 2.0D));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.MAX_STUN_ARMOR.get()).setBaseValue(8.0F);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.PIGLIN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_PIGLIN;
	}
	
	public void setAIAsUnarmed() {
		
	}
	
	public void setAIAsArmed() {
		
	}
	
	public void setAIAsMounted(Entity ridingEntity) {
		
	}
	
	public void setAIAsRange() {
		
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);
		return VisibleMatrix4f.scale(new Vec3f(1.2F, 1.2F, 1.2F), mat, mat);
	}
}