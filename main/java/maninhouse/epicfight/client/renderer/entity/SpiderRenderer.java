package maninhouse.epicfight.client.renderer.entity;

import maninhouse.epicfight.capabilities.entity.mob.SpiderData;
import maninhouse.epicfight.client.renderer.layer.EyeLayer;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.util.ResourceLocation;

public class SpiderRenderer extends ArmatureRenderer<SpiderEntity, SpiderData<SpiderEntity>> {
	private static final ResourceLocation SPIDER_TEXTURE = new ResourceLocation("textures/entity/spider/spider.png");
	private static final ResourceLocation SPIDER_EYE_TEXTURE = new ResourceLocation("textures/entity/spider_eyes.png");
	
	public SpiderRenderer() {
		this.layers.add(new EyeLayer<>(SPIDER_EYE_TEXTURE));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(SpiderEntity entityIn) {
		return SPIDER_TEXTURE;
	}
}