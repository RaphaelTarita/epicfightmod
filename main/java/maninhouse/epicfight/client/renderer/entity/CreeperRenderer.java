package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.CreeperData;
import maninhouse.epicfight.model.Armature;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperRenderer extends ArmatureRenderer<CreeperEntity, CreeperData> {
	public static final ResourceLocation CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
	
	@Override
	protected ResourceLocation getEntityTexture(CreeperEntity entityIn) {
		return CREEPER_TEXTURE;
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, CreeperEntity entityIn, CreeperData entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
}