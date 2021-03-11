package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.capabilities.entity.LivingData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class SimpleTexturBipedRenderer<E extends LivingEntity, T extends LivingData<E>> extends BipedRenderer<E, T> {
	public final ResourceLocation textureLocation;
	
	public SimpleTexturBipedRenderer(String texturePath) {
		textureLocation = new ResourceLocation(texturePath);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(E entityIn) {
		return textureLocation;
	}
}
