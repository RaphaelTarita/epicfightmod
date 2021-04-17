package maninhouse.epicfight.animation;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.gamedata.Animations;

public abstract class Animator {
	protected LivingData<?> entitydata;
	
	public abstract void playAnimation(int id, float modifyTime);
	public abstract void playAnimation(StaticAnimation nextAnimation, float modifyTime);
	public abstract void vacateCurrentPlay();
	public abstract void update();
	public abstract void onEntityDeath();
	public abstract AnimationPlayer getPlayer();
	public abstract void reserveAnimation(StaticAnimation nextAnimation);
	/**
	 * Use this if you don't know which layer you want to get(only effective in client animator)
	 */
	public abstract AnimationPlayer getPlayerFor(StaticAnimation animation);
	
	public boolean isReverse() {
		return false;
	}
	
	public void playDeathAnimation() {
		this.playAnimation(Animations.BIPED_DEATH, 0);
	}
}