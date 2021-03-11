package maninhouse.epicfight.animation;

import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.LinkAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.config.ConfigurationIngame;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;

public class AnimatorServer extends Animator
{
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	private LinkAnimation linkAnimation;
	public boolean pause = false;
	
	public AnimatorServer(LivingData<?> modEntity)
	{
		this.entitydata = modEntity;
		this.linkAnimation = new LinkAnimation();
		this.animationPlayer = new AnimationPlayer(Animations.DUMMY_ANIMATION);
	}
	
	/** Play an animation by animation id **/
	@Override
	public void playAnimation(int id, float modifyTime)
	{
		this.playAnimation(Animations.findAnimationDataById(id), modifyTime);
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime)
	{
		pause = false;
		animationPlayer.getPlay().onFinish(entitydata, animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		nextAnimation.getLinkAnimation(Animations.DUMMY_ANIMATION.getPoseByTime(entitydata, 0), modifyTime, entitydata, linkAnimation);
		linkAnimation.putOnPlayer(animationPlayer);
		nextPlaying = nextAnimation;
	}
	
	@Override
	public void vacateCurrentPlay()
	{
		animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	@Override
	public void update()
	{
		if(pause)
		{
			return;
		}
		
		animationPlayer.update(ConfigurationIngame.A_TICK * animationPlayer.getPlay().getPlaySpeed(entitydata));
		animationPlayer.getPlay().onUpdate(entitydata);
		
		if(animationPlayer.isEnd())
		{
			animationPlayer.getPlay().onFinish(entitydata, true);
			
			if(nextPlaying == null)
			{
				Animations.DUMMY_ANIMATION.putOnPlayer(animationPlayer);
				pause = true;
			}
			else
			{
				nextPlaying.putOnPlayer(animationPlayer);
				nextPlaying = null;
			}
		}
	}
	
	public VisibleMatrix4f getColliderTransformMatrix(int indexer)
	{
		if(indexer == -1)
			return new VisibleMatrix4f();
		return applyPoseToCollider(this.getCurrentPose(), this.entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature().getJointHierarcy(), new VisibleMatrix4f(), indexer);
	}
	
	private VisibleMatrix4f applyPoseToCollider(Pose pose, Joint joint, VisibleMatrix4f parentTransform, int indexer)
	{
		VisibleMatrix4f currentLocalTransform = pose.getTransformByName(joint.getName()).toTransformMatrix();
		VisibleMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
		VisibleMatrix4f bindTransform = VisibleMatrix4f.mul(parentTransform, currentLocalTransform, null);
		VisibleMatrix4f.mul(bindTransform, joint.getAnimatedTransform(), bindTransform);
		indexer = indexer >> 5;
		
		if(indexer == 0)
			return bindTransform;
		else
			return applyPoseToCollider(pose, joint.getSubJoints().get((indexer & 31) - 1), bindTransform, indexer);	
	}
	
	protected Pose getCurrentPose()
	{
		return this.animationPlayer.getCurrentPose(entitydata, 0.5F);
	}
	
	@Override
	public void onEntityDeath()
	{
		if(animationPlayer.getPlay() != null)
		{
			animationPlayer.getPlay().onFinish(entitydata, animationPlayer.isEnd());
		}
		
		if(nextPlaying != null)
		{
			nextPlaying.onFinish(entitydata, false);
		}
	}
	
	@Override
	public AnimationPlayer getPlayer()
	{
		return this.animationPlayer;
	}
	
	@Override
	public AnimationPlayer getPlayerFor(StaticAnimation animation)
	{
		return this.animationPlayer;
	}
}