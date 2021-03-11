package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.capabilities.entity.LivingData;

public class HitAnimation extends ActionAnimation {
	public HitAnimation(int id, float convertTime, String path) {
		super(id, convertTime, false, false, path);
	}

	@Override
	public LivingData.EntityState getState(float time) {
		return LivingData.EntityState.HIT;
	}
}