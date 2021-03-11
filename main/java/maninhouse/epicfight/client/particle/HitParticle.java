package maninhouse.epicfight.client.particle;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class HitParticle extends SpriteTexturedParticle
{
	public static final IParticleRenderType HIT_PARTICLE_TYPE = new IParticleRenderType()
	{
		public void beginRender(BufferBuilder p_217600_1_, TextureManager p_217600_2_)
		{
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.depthMask(false);
			
	        p_217600_2_.bindTexture(AtlasTexture.LOCATION_PARTICLES_TEXTURE);
	        RenderHelper.disableStandardItemLighting();
	        p_217600_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		}
		
		public void finishRender(Tessellator p_217599_1_)
	    {
			p_217599_1_.draw();
	    }
		
		@Override
	    public String toString()
	    {
	    	return "MOD_PARTICLE";
	    }
	};
	
	protected final IAnimatedSprite animatedSprite;
	
	public HitParticle(ClientWorld world, double x, double y, double z, IAnimatedSprite animatedSprite)
	{
		super(world, x, y, z);
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
		this.animatedSprite = animatedSprite;
		this.selectSpriteWithAge(animatedSprite);
	}
	
	@Override
	public void tick()
	{
		this.prevPosX = this.posX;
	    this.prevPosY = this.posY;
	    this.prevPosZ = this.posZ;
	    
	    if (this.age++ >= this.maxAge)
	    {
	       this.setExpired();
	    }
	    else
	    {
	       this.selectSpriteWithAge(this.animatedSprite);
	    }
	}
	
	@Override
	public IParticleRenderType getRenderType()
	{
		return HIT_PARTICLE_TYPE;
	}
	
	@Override
	public int getBrightnessForRender(float partialTick)
	{
		return 15728880;
	}
}