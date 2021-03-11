package maninhouse.epicfight.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class GreatswordItem extends WeaponItem {
	protected static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("16295ED8-B092-4A75-9A94-BCD8D56668BB");
	
	public GreatswordItem(Item.Properties build) {
		super(ModItemTier.GREATSWORD, 0, 0.0F, build);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return toRepair.getItem() == Items.IRON_BLOCK;
	}

	@Override
	public int getItemEnchantability() {
		return 5;
	}

	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability() {
    	capability = new ModWeaponCapability(WeaponCategory.GREATSWORD, (playerdata)->Skills.GIANT_WHIRLWIND, null, Sounds.WHOOSH_BIG, Sounds.BLADE_HIT,
    			Colliders.greatSword, 0, 0, 1, true, false);
    	capability.addTwohandAutoAttackCombos(Animations.GREATSWORD_AUTO_1);
    	capability.addTwohandAutoAttackCombos(Animations.GREATSWORD_AUTO_2);
    	capability.addTwohandAutoAttackCombos(Animations.GREATSWORD_DASH);
    	capability.setTwoHandStyleAttribute(0.0D, 4.3D, 4);
    	capability.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.JUMPING, Animations.BIPED_JUMP_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.KNEELING, Animations.BIPED_KNEEL_MASSIVE_HELD);
    	capability.addLivingMotionChanger(LivingMotion.SNEAKING, Animations.BIPED_SNEAK_MASSIVE_HELD);
    }
    
    @Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		if (slot == EquipmentSlotType.MAINHAND) {
    		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 13.0D, Operation.ADDITION));
    		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.0D, Operation.ADDITION));
            builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_MODIFIER, "Weapon modifier", -0.02D, Operation.ADDITION));
    	    return builder.build();
        }
        
        return super.getAttributeModifiers(slot, stack);
    }
}
