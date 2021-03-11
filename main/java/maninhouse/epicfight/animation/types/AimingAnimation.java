package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.Quaternion;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.collada.AnimationDataExtractor;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class AimingAnimation extends StaticAnimation {
	public StaticAnimation lookUp;
	public StaticAnimation lookDown;

	public AimingAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2, String path3) {
		super(id, convertTime, repeatPlay, path1);
		lookUp = new StaticAnimation(path2);
		lookDown = new StaticAnimation(path3);
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		
		AnimatorClient animator = entitydata.getClientAnimator();
		if (animator.mixLayerActivated) {
			AnimationPlayer player = animator.getMixLayerPlayer();
			if (player.getElapsedTime() >= this.totalTime - 0.06F) {
				animator.mixLayer.pause = true;
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		if (entitydata.isFirstPerson()) {
			return super.getPoseByTime(entitydata, time);
		} else {
			float pitch = entitydata.getOriginalEntity().getPitch(Minecraft.getInstance().getRenderPartialTicks());
			
			StaticAnimation interpolateAnimation;
			interpolateAnimation = (pitch > 0) ? this.lookDown : this.lookUp;
			
			Pose pose1 = getPoseByTime(time);
			Pose pose2 = interpolateAnimation.getPoseByTime(entitydata, time);
			Pose interpolatedPose = Pose.interpolatePose(pose1, pose2, (Math.abs(pitch) / 90.0F));
			JointTransform chest = interpolatedPose.getTransformByName("Chest");
			JointTransform head = interpolatedPose.getTransformByName("Head");
			
			float f = 90.0F;
			float ratio = (f - Math.abs(entitydata.getOriginalEntity().rotationPitch)) / f;
			float yawOffset = entitydata.getOriginalEntity().getRidingEntity() != null ? entitydata.getOriginalEntity().rotationYaw : entitydata.getOriginalEntity().renderYawOffset;
			chest.setRotation(Quaternion.rotate((float)-Math.toRadians((entitydata.getOriginalEntity().rotationYaw - yawOffset) * ratio), new Vec3f(0,1,0), chest.getRotation()));
			head.setRotation(Quaternion.rotate((float)-Math.toRadians((yawOffset - entitydata.getOriginalEntity().rotationYaw) * ratio), new Vec3f(0,1,0), head.getRotation()));
			return interpolatedPose;
		}
	}
	
	private Pose getPoseByTime(float time) {
		Pose pose = new Pose();
		for (String jointName : jointTransforms.keySet()) {
			pose.putJointData(jointName, jointTransforms.get(jointName).getInterpolatedTransform(time));
		}
		
		return pose;
	}
	
	@Override
	public StaticAnimation bindFull(Armature armature) {
		if (animationDataPath != null) {
			AnimationDataExtractor.extractAnimation(new ResourceLocation(EpicFightMod.MODID, animationDataPath), this, armature);
			animationDataPath = null;
			AnimationDataExtractor.extractAnimation(new ResourceLocation(EpicFightMod.MODID, lookUp.animationDataPath), lookUp, armature);
			lookUp.animationDataPath = null;
			AnimationDataExtractor.extractAnimation(new ResourceLocation(EpicFightMod.MODID, lookDown.animationDataPath), lookDown, armature);
			lookDown.animationDataPath = null;
		}
		return this;
	}
}