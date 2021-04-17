package maninhouse.epicfight.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HandProperty;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WieldStyle;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class TachiItem extends WeaponItem {
	public TachiItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 4, -2.6F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
    	int harvestLevel = tier.getHarvestLevel();
		ModWeaponCapability weaponCapability = new ModWeaponCapability(WeaponCategory.TACHI, (playerdata)-> WieldStyle.TWO_HAND, null, Sounds.WHOOSH, Sounds.BLADE_HIT,
    			Colliders.spearNarrow, HandProperty.TWO_HANDED);
		weaponCapability.addStyleCombo(WieldStyle.TWO_HAND, Animations.TACHI_AUTO_1, Animations.TACHI_AUTO_2, Animations.TACHI_DASH);
		weaponCapability.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
		weaponCapability.addStyleAttributeSimple(WieldStyle.TWO_HAND, 0.0D, 1.0D + harvestLevel * 0.5D, 2);
		weaponCapability.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.LETHAL_SLICING);
		weaponCapability.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_TACHI);
		weaponCapability.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_TACHI);
		weaponCapability.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_TACHI);
		weaponCapability.addLivingMotionChanger(LivingMotion.SNEAKING, Animations.BIPED_SNEAK_TACHI);
		weaponCapability.addLivingMotionChanger(LivingMotion.JUMPING, Animations.BIPED_JUMP_TACHI);
		this.capability = weaponCapability;
    }
}