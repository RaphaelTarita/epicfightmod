package maninhouse.epicfight.particle;

import java.util.function.BiFunction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class HitParticleType extends BasicParticleType
{
	public static final BiFunction<Entity, Entity, Vector3d> DEFAULT = (e1, e2) -> {
		EntitySize size = e1.getSize(e1.getPose());
		return new Vector3d(size.width, size.height, 0.0D);
	};
	
	public static final BiFunction<Entity, Entity, Vector3d> DIRECTIONAL = (e1, e2) -> {
		return new Vector3d(e2.getPitch(0.5F), e2.getYaw(0.5F), 0.0D);
	};
	
	public BiFunction<Entity, Entity, Vector3d> defaultGetter;
	
	public HitParticleType(boolean p_i50791_1_)
	{
		this(p_i50791_1_, DEFAULT);
	}
	
	public HitParticleType(boolean p_i50791_1_, BiFunction<Entity, Entity, Vector3d> argumentGetter)
	{
		super(p_i50791_1_);
		this.defaultGetter = argumentGetter;
	}
	
	public void spawnParticleWithArgument(ServerWorld world, BiFunction<Entity, Entity, Vector3d> argumentGetter, Entity e1, Entity e2)
	{
		Vector3d arguments = argumentGetter == null ? this.defaultGetter.apply(e1, e2) : argumentGetter.apply(e1, e2);
		world.spawnParticle(this, e1.getPosX(), e1.getPosY(), e1.getPosZ(), 0, arguments.x, arguments.y, arguments.z, 1.0D);
	}
}