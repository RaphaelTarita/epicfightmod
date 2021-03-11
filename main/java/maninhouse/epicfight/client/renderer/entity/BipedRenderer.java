package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.renderer.layer.HeldItemLayer;
import maninhouse.epicfight.client.renderer.layer.WearableItemLayer;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BipedRenderer<E extends LivingEntity, T extends LivingData<E>> extends ArmatureRenderer<E, T> {
	public BipedRenderer() {
		this.layers.add(new HeldItemLayer<>());
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.HEAD));
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.CHEST));
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.LEGS));
		this.layers.add(new WearableItemLayer<>(EquipmentSlotType.FEET));
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		if (entityIn.isSneaking()) {
			matStack.translate(0.0D, 0.15D, 0.0D);
		}
		if (entityIn.isChild()) {
			this.transformJoint(9, armature, new VisibleMatrix4f().scale(new Vec3f(1.25F, 1.25F, 1.25F)));
		}
		
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}