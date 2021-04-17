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

public class SpearItem extends WeaponItem {
	public SpearItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 3, -2.8F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
    	int harvestLevel = tier.getHarvestLevel();
		ModWeaponCapability weaponCapability = new ModWeaponCapability(WeaponCategory.SPEAR, (playerdata)-> playerdata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? WieldStyle.TWO_HAND : WieldStyle.ONE_HAND,
    			null, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.spearNarrow, HandProperty.MAINHAND_ONLY);
		weaponCapability.addStyleCombo(WieldStyle.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH);
		weaponCapability.addStyleCombo(WieldStyle.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH);
		weaponCapability.addStyleCombo(WieldStyle.MOUNT, Animations.SPEAR_MOUNT_ATTACK);
		weaponCapability.addStyleAttributeSimple(WieldStyle.ONE_HAND, 4.0D + 4.0D * harvestLevel, 2.4D + harvestLevel * 0.3D, 1);
		weaponCapability.addStyleAttributeSimple(WieldStyle.TWO_HAND, 0.0D, 0.6D + harvestLevel * 0.5D, 3);
		weaponCapability.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.HEARTPIERCER);
		weaponCapability.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.SLAUGHTER_STANCE);
		weaponCapability.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_HELDING_WEAPON);
		this.capability = weaponCapability;
    }
}