package maninhouse.epicfight.capabilities.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.item.Item;

public class CrossbowCapability extends RangedWeaponCapability
{
	public CrossbowCapability(Item item)
	{
		super(item, Animations.BIPED_CROSSBOW_RELOAD, Animations.BIPED_CROSSBOW_AIM, Animations.BIPED_CROSSBOW_SHOT);
		this.rangeAnimationSet.put(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.WALKING, Animations.BIPED_WALK_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.RUNNING, Animations.BIPED_WALK_CROSSBOW);
	}
	
	@Override
	public boolean isTwoHanded()
	{
		return true;
	}
}