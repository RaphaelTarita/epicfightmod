package maninhouse.epicfight.item;

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

public class KnuckleItem extends WeaponItem {
	public KnuckleItem(Item.Properties build, IItemTier materialIn) {
		super(materialIn, 2, 0.0F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
		ModWeaponCapability weaponCapability = new ModWeaponCapability(WeaponCategory.FIST, (playerdata)-> WieldStyle.ONE_HAND, null, Sounds.WHOOSH, Sounds.BLUNT_HIT,
    			Colliders.fist, HandProperty.GENERAL);
		weaponCapability.addStyleCombo(WieldStyle.ONE_HAND, Animations.FIST_AUTO_1, Animations.FIST_AUTO_2, Animations.FIST_AUTO_3, Animations.FIST_DASH);
		weaponCapability.addStyleAttributeSimple(WieldStyle.ONE_HAND, 0.0D, 1.0D, 1);
		weaponCapability.addStyleSpecialAttack(WieldStyle.ONE_HAND, Skills.RELENTLESS_COMBO);
		this.capability = weaponCapability;
    }
}