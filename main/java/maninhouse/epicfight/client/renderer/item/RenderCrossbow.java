package maninhouse.epicfight.client.renderer.item;

import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCrossbow extends RenderShootableWeapon
{
	public RenderCrossbow()
	{
		correctionMatrix = new VisibleMatrix4f();
	}
}