package maninhouse.epicfight.utils.game;

public class Formulars
{
	public static double getAttackSpeedPanelty(double weight, double weaponAttackSpeed)
	{
		if(weight > 40.0D)
			return -0.1D * (weight / 40.0D) * (Math.max(weaponAttackSpeed - 0.8D, 0.0D) * 1.5D);
		else
			return 0.0D;
	}
	
	public static float getRollAnimationSpeedPanelty(float weight)
	{
		return 1.0F + (60.0F - weight) / (weight * 2.0F);
	}
}