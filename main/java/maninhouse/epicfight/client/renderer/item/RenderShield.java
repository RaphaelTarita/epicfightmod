package maninhouse.epicfight.client.renderer.item;

import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShield extends RenderItemMirror
{
	public RenderShield()
	{
		super();
		leftHandCorrectionMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.rotate((float)Math.toRadians(180D), new Vec3f(0F,1F,0F), leftHandCorrectionMatrix, leftHandCorrectionMatrix);
		VisibleMatrix4f.rotate((float)Math.toRadians(-90D), new Vec3f(1F,0F,0F), leftHandCorrectionMatrix, leftHandCorrectionMatrix);
		VisibleMatrix4f.translate(new Vec3f(0F,0.1F,0F), leftHandCorrectionMatrix, leftHandCorrectionMatrix);
		
		VisibleMatrix4f.translate(new Vec3f(0F,0.1F,0F), correctionMatrix, correctionMatrix);
	}
}