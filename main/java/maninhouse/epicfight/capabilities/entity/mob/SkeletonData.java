package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.capabilities.entity.DataKeys;
import maninhouse.epicfight.capabilities.entity.IRangedAttackMobCapability;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.game.IndirectDamageSourceExtended;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;

public class SkeletonData<T extends AbstractSkeletonEntity> extends BipedMobData<T> implements IRangedAttackMobCapability {
	public SkeletonData() {
		super(Faction.UNDEAD);
	}
	
	public SkeletonData(Faction faction) {
		super(faction);
	}
	
	@Override
	public boolean onEntityJoinWorld(T entityIn) {
		if(super.onEntityJoinWorld(entityIn)) {
			this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(0));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.commonBipedCreatureAnimatorInit(animatorClient);
		super.initAnimator(animatorClient);
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		float posY = 0;
		
		if (orgEntity.getRidingEntity() != null) {
			posY = 0.45F;
		}
		
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);
		return VisibleMatrix4f.translate(new Vec3f(0, posY, 0), mat, mat);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_SKELETON;
	}
	
	@Override
	public IndirectDamageSourceExtended getRangedDamageSource(Entity damageCarrier) {
		IndirectDamageSourceExtended source = new IndirectDamageSourceExtended("arrow", this.orgEntity, damageCarrier, StunType.SHORT);
		source.setImpact(1.0F);
		
		return source;
	}
}