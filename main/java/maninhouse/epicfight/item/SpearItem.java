package maninhouse.epicfight.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;

public class SpearItem extends WeaponItem
{
	public SpearItem(Item.Properties build, ItemTier materialIn)
	{
		super(materialIn, 3, -2.8F, build);
		this.setStats();
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn)
    {
        return false;
    }
    
    @Override
    public void setWeaponCapability()
    {
    	
    }
    
    public void setStats()
    {
    	double oneHandImpact = 2.4D + this.getTier().getHarvestLevel() * 0.3D;
    	double twoHandImpact = 0.6D + this.getTier().getHarvestLevel() * 0.5D;
    	
    	capability = new ModWeaponCapability(WeaponCategory.SPEAR, (playerdata)-> playerdata.getOriginalEntity().getHeldItemOffhand() == ItemStack.EMPTY ? Skills.SLAUGHTER_STANCE : Skills.HEARTPIERCER,
    			null, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.spearNarrow, 4.0D + 4.0D * this.getTier().getHarvestLevel(), oneHandImpact, 1, false, true);
    	
    	capability.addAutoAttackCombos(Animations.SPEAR_ONEHAND_AUTO);
    	capability.addAutoAttackCombos(Animations.SPEAR_DASH);
    	capability.addTwohandAutoAttackCombos(Animations.SPEAR_TWOHAND_AUTO_1);
    	capability.addTwohandAutoAttackCombos(Animations.SPEAR_TWOHAND_AUTO_2);
    	capability.addTwohandAutoAttackCombos(Animations.SPEAR_DASH);
    	capability.addMountAttackCombos(Animations.SPEAR_MOUNT_ATTACK);
    	capability.setTwoHandStyleAttribute(0, twoHandImpact, 3);
    	capability.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_HELDING_WEAPON);
    }
}