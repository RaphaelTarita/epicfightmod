package maninhouse.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.ModRenderTypes;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EyeLayer<E extends LivingEntity, T extends LivingData<E>> extends Layer<E, T> {
	private final RenderType renderType;
	
	public EyeLayer(ResourceLocation eyeTexture) {
		this.renderType = ModRenderTypes.getEyes(eyeTexture);
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, VisibleMatrix4f[] poses, float partialTicks) {
		IVertexBuilder ivertexbuilder = buffer.getBuffer(renderType);
		entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).draw(matrixStackIn, ivertexbuilder, 15728640, 1.0F, 1.0F, 1.0F, 1.0F, poses);
	}
}