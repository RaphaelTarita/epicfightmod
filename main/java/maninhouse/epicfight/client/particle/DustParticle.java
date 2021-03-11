package maninhouse.epicfight.client.particle;

import maninhouse.epicfight.utils.math.MathUtils;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DustParticle extends HitParticle
{
	public DustParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn,
			IAnimatedSprite animatedSprite)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, animatedSprite);
		this.posX = xCoordIn + (rand.nextDouble() - 0.5D);
		this.posY = yCoordIn + (rand.nextDouble() + 1.5F);
		this.posZ = zCoordIn + (rand.nextDouble() - 0.5D);
	    this.particleRed = 1.0F;
	    this.particleGreen = 1.0F;
	    this.particleBlue = 1.0F;
	    this.particleScale = rand.nextFloat() * 0.05F;
		this.maxAge = 4 + rand.nextInt(4);
		this.canCollide = false;
		
		Vector3d rot = MathUtils.getVectorForRotation(rand.nextFloat() * 60.0F - 30.0F, (float)ySpeedIn+rand.nextFloat() * 60.0F - 30.0F);
		rot.scale(rand.nextDouble()+0.5D);
		this.motionX = rot.x;
		this.motionY = rot.y;
		this.motionZ = rot.z;
	}
	
	@Override
	public void tick()
	{
		this.prevPosX = this.posX;
	    this.prevPosY = this.posY;
	    this.prevPosZ = this.posZ;
	    //System.out.println(this.particleScale);
	    if (this.age++ >= this.maxAge)
	    {
	    	this.setExpired();
	    }
	    else
	    {
	        this.move(this.motionX, this.motionY, this.motionZ);
	        this.motionX *= (double)0.98F;
	        this.motionY *= (double)0.98F;
	        this.motionZ *= (double)0.98F;
	    }
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType>
	{
		protected IAnimatedSprite sprite;
		
		public Factory(IAnimatedSprite sprite)
		{
			this.sprite = sprite;
		}
		
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
		{
			return new DustParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, sprite);
		}
	}
}