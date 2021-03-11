package maninhouse.epicfight.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParryParticle extends HitParticle
{
	public ParryParticle(ClientWorld world, double x, double y, double z, double width, double height, double _null, IAnimatedSprite animatedSprite)
	{
		super(world, x, y, z, animatedSprite);
		this.posX = x + (rand.nextDouble() - 0.5D) * width;
		this.posY = y + (rand.nextDouble() + height) * 0.5;
		this.posZ = z + (rand.nextDouble() - 0.5D) * width;
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = 1.0F;
		this.maxAge = 6;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType>
	{
		private final IAnimatedSprite spriteSet;
		
	    public Factory(IAnimatedSprite spriteSet)
	    {
	         this.spriteSet = spriteSet;
	    }
	    
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
		{
			HitBluntParticle particle = new HitBluntParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
			return particle;
		}
	}
}
