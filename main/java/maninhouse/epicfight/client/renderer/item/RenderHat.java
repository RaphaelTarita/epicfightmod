package maninhouse.epicfight.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHat extends RenderItemBase {
	@Override
	public void renderItemOnHead(ItemStack stack, LivingData<?> itemHolder, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight, float partialTicks) {
		EntityRenderer<?> render = Minecraft.getInstance().getRenderManager().getRenderer(itemHolder.getOriginalEntity());
		if(render instanceof LivingRenderer && ((LivingRenderer<?, ?>)render).getEntityModel() instanceof BipedModel) {
			ModelRenderer model = ((BipedModel<?>)((LivingRenderer<?, ?>)render).getEntityModel()).bipedHead;
			@SuppressWarnings({ "unchecked", "rawtypes" })
			HeadLayer<LivingEntity, ?> layer = new HeadLayer(((LivingRenderer<?, ?>)render));
			LivingEntity entity = itemHolder.getOriginalEntity();
			VisibleMatrix4f modelMatrix = new VisibleMatrix4f();
			VisibleMatrix4f.scale(new Vec3f(-0.94F, -0.94F, 0.94F), modelMatrix, modelMatrix);
			if(itemHolder.getOriginalEntity().isChild()) {
				VisibleMatrix4f.translate(new Vec3f(0.0F, -0.65F, 0.0F), modelMatrix, modelMatrix);
			}
			VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(9).getAnimatedTransform(), modelMatrix, modelMatrix);
			model.rotateAngleX = 0;
			model.rotateAngleY = 0;
			model.rotateAngleZ = 0;
			model.rotationPointX = 0;
			model.rotationPointY = 0;
			model.rotationPointZ = 0;
			VisibleMatrix4f transpose = VisibleMatrix4f.transpose(modelMatrix, null);
			viewMatrixStack.push();
			MathUtils.translateStack(viewMatrixStack, modelMatrix);
			MathUtils.rotateStack(viewMatrixStack, transpose);
			layer.render(viewMatrixStack, buffer, packedLight, entity, 0F, 0F, 0F, 0F, 0F, 0F);
			viewMatrixStack.pop();
		}
	}
}