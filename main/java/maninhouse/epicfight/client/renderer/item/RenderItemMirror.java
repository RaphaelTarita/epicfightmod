package maninhouse.epicfight.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderItemMirror extends RenderItemBase {
	protected VisibleMatrix4f leftHandCorrectionMatrix;
	
	@Override
	public void renderItemInHand(ItemStack stack, LivingData<?> itemHolder, Hand hand, IRenderTypeBuffer buffer, MatrixStack viewMatrixStack, int packedLight) {
		VisibleMatrix4f modelMatrix = new VisibleMatrix4f(hand == Hand.OFF_HAND ? leftHandCorrectionMatrix : correctionMatrix);
		String heldingHand = hand == Hand.MAIN_HAND ? "Tool_R" : "Tool_L";
		VisibleMatrix4f.mul(itemHolder.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointByName(heldingHand).getAnimatedTransform(), modelMatrix, modelMatrix);
		VisibleMatrix4f transpose = VisibleMatrix4f.transpose(modelMatrix, null);
		
		MathUtils.translateStack(viewMatrixStack, modelMatrix);
		MathUtils.rotateStack(viewMatrixStack, transpose);
		
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, viewMatrixStack, buffer);
	}
}