package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.EndermanData;
import maninhouse.epicfight.client.renderer.layer.EyeLayer;
import maninhouse.epicfight.client.renderer.layer.HeldItemLayer;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanRenderer extends ArmatureRenderer<EndermanEntity, EndermanData> {
	private static final ResourceLocation ENDERMAN_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman.png");
	private static final ResourceLocation ENDERMAN_EYE_TEXTURE = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	
	public EndermanRenderer() {
		this.layers.add(new EyeLayer<>(ENDERMAN_EYE_TEXTURE));
		this.layers.add(new HeldItemLayer<>());
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, EndermanEntity entityIn, EndermanData entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(15, armature, entitydata.getHeadMatrix(partialTicks));
		
		if (entitydata.isRaging()) {
			VisibleMatrix4f head = new VisibleMatrix4f();
			VisibleMatrix4f.translate(new Vec3f(0, 0.25F, 0), head, head);
			transformJoint(16, armature, head);
		}
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EndermanEntity entityIn) {
		return ENDERMAN_TEXTURE;
	}
}