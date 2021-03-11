package maninhouse.epicfight.animation.types.attack;

import javax.annotation.Nullable;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class TargetTraceAnimation extends AttackAnimation
{
	public TargetTraceAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			@Nullable Collider collider, String index, String path)
	{
		this(id, convertTime, antic, preDelay, contact, recovery, affectY, Hand.MAIN_HAND, collider, index, path);
	}
	
	public TargetTraceAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			Hand hand, @Nullable Collider collider, String index, String path)
	{
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public TargetTraceAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases)
	{
		super(id, convertTime, affectY, path, phases);
	}
	
	@Override
	protected Vec3f getCoordVector(LivingData<?> entitydata) {
		EntityState state = this.getState(entitydata.getAnimator().getPlayer().getElapsedTime());
		Vec3f vec3 = super.getCoordVector(entitydata);
		if(state.getLevel() < 3) {
			LivingEntity orgEntity = entitydata.getOriginalEntity();
			LivingEntity target = entitydata.getAttackTarget();
			float multiplier = (orgEntity instanceof PlayerEntity) ? 2.0F : 1.0F;
			
			if (target != null) {
				float distance = Math.max(Math.min(orgEntity.getDistance(target) - orgEntity.getWidth() - target.getWidth(), multiplier), 0.0F);
				vec3.x *= distance;
				vec3.z *= distance;
			} else {
				vec3.x *= 0.5F;
				vec3.z *= 0.5F;
			}
		}
		
		return vec3;
	}
}