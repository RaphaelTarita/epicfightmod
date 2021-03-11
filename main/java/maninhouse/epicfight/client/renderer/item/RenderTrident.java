package maninhouse.epicfight.client.renderer.item;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class RenderTrident extends RenderItemBase
{
	private VisibleMatrix4f correctionMatrixReverse = new VisibleMatrix4f();
	
	public RenderTrident()
	{
		correctionMatrix = new VisibleMatrix4f();
		VisibleMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), correctionMatrix, correctionMatrix);
		VisibleMatrix4f.translate(new Vec3f(0.0F,0.1F,0.0F), correctionMatrix, correctionMatrix);
		
		VisibleMatrix4f.rotate((float)Math.toRadians(-80), new Vec3f(1,0,0), correctionMatrixReverse, correctionMatrixReverse);
		VisibleMatrix4f.translate(new Vec3f(0.0F,0.1F,0.0F), correctionMatrixReverse, correctionMatrixReverse);
	}
	
	@Override
	public VisibleMatrix4f getCorrectionMatrix(ItemStack stack, LivingData<?> itemHolder, Hand hand)
	{
		if(itemHolder.getOriginalEntity().getItemInUseCount() > 0)
		{
			return new VisibleMatrix4f(this.correctionMatrixReverse);
		}
		else
		{
			VisibleMatrix4f mat = new VisibleMatrix4f(correctionMatrix);
			VisibleMatrix4f.translate(new Vec3f(0.0F, 0.4F, 0.0F), mat, mat);
			return mat;
		}
	}
}