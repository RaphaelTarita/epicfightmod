package maninhouse.epicfight.client.animation;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.LinkAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.config.ConfigurationIngame;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseLayer {
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	protected LinkAnimation linkAnimation = new LinkAnimation();
	
	public boolean pause;
	
	public BaseLayer(DynamicAnimation animation) {
		this.animationPlayer = new AnimationPlayer(animation);
		this.nextPlaying = new StaticAnimation();
	}

	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float modifyTime) {
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		setLinkAnimation(nextAnimation, entitydata, modifyTime);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = nextAnimation;
	}
	
	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata) {
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = null;
	}
	
	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float timeModifier) {
		Pose currentPose = this.animationPlayer.getCurrentPose(entitydata, Minecraft.getInstance().getRenderPartialTicks());
		nextAnimation.getLinkAnimation(currentPose, timeModifier, entitydata, this.linkAnimation);
	}
	
	public void update(LivingData<?> entitydata) {
		if (pause) {
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}
		
		float frameTime = ConfigurationIngame.A_TICK * this.animationPlayer.getPlay().getPlaySpeed(entitydata);
		
		this.animationPlayer.update(frameTime);
		this.animationPlayer.getPlay().onUpdate(entitydata);
		
		if (this.animationPlayer.isEnd()) {
			if (nextPlaying != null) {
				float exceedTime = this.animationPlayer.getExceedTime();
				this.animationPlayer.getPlay().onFinish(entitydata, true);
				this.nextPlaying.putOnPlayer(this.animationPlayer);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime);
				this.nextPlaying = null;
			}
		}
	}
	
	public void clear(LivingData<?> entitydata) {
		if (animationPlayer.getPlay() != null) {
			animationPlayer.getPlay().onFinish(entitydata, animationPlayer.isEnd());
		}

		if (nextPlaying != null) {
			nextPlaying.onFinish(entitydata, false);
		}
	}
}