package maninhouse.epicfight.capabilities.entity;

import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;

public abstract class CapabilityEntity<T extends Entity> {
	protected T orgEntity;

	public abstract void update();

	protected abstract void updateOnClient();

	protected abstract void updateOnServer();

	public void postInit() {
	}

	public void onEntityConstructed(T entityIn) {
		this.orgEntity = entityIn;
	}

	public boolean onEntityJoinWorld(T entityIn) {
		return this.orgEntity == entityIn;
	}

	public T getOriginalEntity() {
		return orgEntity;
	}

	public boolean isRemote() {
		return orgEntity.world.isRemote;
	}

	public void aboutToDeath() {

	}
	
	public VisibleMatrix4f getMatrix(float partialTicks) {
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.prevRotationPitch,
				orgEntity.rotationPitch, orgEntity.prevRotationYaw, orgEntity.rotationYaw, partialTicks, 1, 1, 1);
	}

	public abstract VisibleMatrix4f getModelMatrix(float partialTicks);
}