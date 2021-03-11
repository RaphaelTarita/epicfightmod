package maninhouse.epicfight.client.particle;

import maninhouse.epicfight.particle.Particles;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HitCutParticle extends MetaParticle
{
	public HitCutParticle(ClientWorld world, double x, double y, double z, double width, double height, double _null)
	{
		super(world, x, y, z);
		
		this.posX = x + (rand.nextDouble() - 0.5D) * width;
		this.posY = y + (rand.nextDouble() + height) * 0.5;
		this.posZ = z + (rand.nextDouble() - 0.5D) * width;
		this.world.addParticle(Particles.BLOOD.get(), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		this.world.addParticle(Particles.CUT.get(), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType>
	{
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
		{
			HitCutParticle particle = new HitCutParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
			return particle;
		}
	}
}