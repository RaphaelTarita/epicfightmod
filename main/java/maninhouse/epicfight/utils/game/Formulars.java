package maninhouse.epicfight.utils.game;

import maninhouse.epicfight.capabilities.entity.CapabilityEntity;
import maninhouse.epicfight.world.ModGamerules;
import net.minecraft.util.math.MathHelper;

public class Formulars {
	public static double getAttackSpeedPenalty(double weight, double weaponAttackSpeed, CapabilityEntity<?> entity) {
		double attenuation = (double)MathHelper.clamp(entity.getOriginalEntity().world.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT), 0, 100) / 100.0D;
		if(weight > 40.0D) {
			return -0.1D * (weight / 40.0D) * (Math.max(weaponAttackSpeed - 0.8D, 0.0D) * 1.5D) * attenuation;
		} else { 
			return 0.0D;
		}
	}
	
	public static float getRollAnimationSpeedPenalty(float weight, CapabilityEntity<?> entity) {
		float attenuation = (float)MathHelper.clamp(entity.getOriginalEntity().world.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT), 0, 100) / 100.0F;
		weight = MathHelper.lerp(attenuation, 40.0F, weight);
		return 1.0F + (60.0F - weight) / (weight * 2.0F);
	}
	
	public static float getSkillRegen(float weight, CapabilityEntity<?> entity) {
		float attenuation = (float)MathHelper.clamp(entity.getOriginalEntity().world.getGameRules().getInt(ModGamerules.SPEED_PENALTY_PERCENT), 0, 100) / 100.0F;
		weight = MathHelper.lerp(attenuation, 40.0F, weight);
		return (float) (40.0F / weight);
	}
}