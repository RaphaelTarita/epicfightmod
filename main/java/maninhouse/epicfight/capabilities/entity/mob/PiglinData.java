package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.DataKeys;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.entity.ai.brain.BrainRemodeler;
import maninhouse.epicfight.entity.ai.brain.task.AttackWithPatternTask;
import maninhouse.epicfight.entity.ai.brain.task.CrossBowAttackTask;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSReqSpawnInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ShootTargetTask;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.tags.ItemTags;

public class PiglinData extends BipedMobData<PiglinEntity> {
	public PiglinData() {
		super(Faction.PIGLIN_ARMY);
	}
	
	@Override
	public void onEntityJoinWorld(PiglinEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		BrainRemodeler.replaceTask(this.orgEntity.getBrain(), Activity.FIGHT, 13, AttackTargetTask.class,
				new AttackWithPatternTask(this, MobAttackPatterns.BIPED_ARMED_ONEHAND, 0.0D, 2.0D));
		BrainRemodeler.replaceTask(this.orgEntity.getBrain(), Activity.FIGHT, 14, ShootTargetTask.class, new CrossBowAttackTask<>());
		BrainRemodeler.removeTask(this.orgEntity.getBrain(), Activity.CELEBRATE, 15, FirstShuffledTask.class);
		this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.0F);
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		
		StaticAnimation randomCelebrate = Animations.findAnimationDataById(Animations.PIGLIN_CELEBRATE1.getId()	+ this.orgEntity.getRNG().nextInt(3));
		
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.CELEBRATE, randomCelebrate);
		animatorClient.addLivingAnimation(LivingMotion.ADMIRE, Animations.PIGLIN_ADMIRE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.PIGLIN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void postInit() {
		super.postInit();

		if (this.isRemote()) {
			AnimatorClient animator = this.getClientAnimator();
			if(orgEntity.isChild()) {
				animator.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_RUN);
			}
			ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
		if (this.getOriginalEntity().getHeldItemOffhand().getItem().isIn(ItemTags.PIGLIN_LOVED))
			this.currentMotion = LivingMotion.ADMIRE;
		else if (this.orgEntity.func_234425_eN_())
			this.currentMotion = LivingMotion.CELEBRATE;
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
}