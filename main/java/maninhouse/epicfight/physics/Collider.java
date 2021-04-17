package maninhouse.epicfight.physics;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.Vec4f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Collider {
	protected final Vec3f modelCenter;
	protected AxisAlignedBB hitboxAABB;
	
	protected Vec3f worldCenter;

	public Collider(Vec3f pos, @Nullable AxisAlignedBB entityCallAABB) {
		this.modelCenter = pos;
		this.hitboxAABB = entityCallAABB;
		this.worldCenter = new Vec3f();
	}

	public void transform(VisibleMatrix4f mat) {
		Vec4f temp = new Vec4f(0,0,0,1);
		
		temp.x = modelCenter.x;
		temp.y = modelCenter.y;
		temp.z = modelCenter.z;
		VisibleMatrix4f.transform(mat, temp, temp);
		worldCenter.x = temp.x;
		worldCenter.y = temp.y;
		worldCenter.z = temp.z;
	}
	
	/** Display on debugging **/
	@OnlyIn(Dist.CLIENT)
	public abstract void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, VisibleMatrix4f pose, float partialTicks, boolean red);
	
	public abstract boolean isCollideWith(Entity opponent);
	
	public void extractHitEntities(List<Entity> entities) {
		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext()) {
			Entity entity = iterator.next();
			if (!isCollideWith(entity)) {
				iterator.remove();
			}
		}
	}

	public Vector3d getCenter() {
		return new Vector3d(worldCenter.x, worldCenter.y, worldCenter.z);
	}

	public AxisAlignedBB getHitboxAABB() {
		return hitboxAABB.offset(-worldCenter.x, worldCenter.y, -worldCenter.z);
	}
}