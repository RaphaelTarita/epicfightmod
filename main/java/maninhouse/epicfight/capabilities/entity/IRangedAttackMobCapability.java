package maninhouse.epicfight.capabilities.entity;

import maninhouse.epicfight.utils.game.IndirectDamageSourceExtended;
import net.minecraft.entity.Entity;

public interface IRangedAttackMobCapability
{
	public abstract IndirectDamageSourceExtended getRangedDamageSource(Entity damageCarrier);
}