package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.capabilities.entity.LivingData;
import net.minecraft.client.Minecraft;

public class MixLinkAnimation extends DynamicAnimation
{
	private Pose lastPose;
	
	public void setLastPose(Pose pose)
	{
		this.lastPose = pose;
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd)
	{
		if(isEnd)
			entitydata.getClientAnimator().mixLayerActivated = false;
		entitydata.getClientAnimator().mixLayer.animationPlayer.resetPlayer();
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time)
	{
		Pose basePose = entitydata.getClientAnimator().getCurrentPose(entitydata.getClientAnimator().baseLayer, Minecraft.getInstance().getRenderPartialTicks());
		return Pose.interpolatePose(lastPose, basePose, time / totalTime);
	}
}