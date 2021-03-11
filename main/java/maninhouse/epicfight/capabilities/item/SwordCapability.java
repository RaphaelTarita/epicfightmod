package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;

public class SwordCapability extends MaterialItemCapability {
	private static List<StaticAnimation> swordAttackMotion;
	private static List<StaticAnimation> dualSwordAttackMotion;
	private SoundEvent hitSound;

	public SwordCapability(Item item) {
		super(item, WeaponCategory.SWORD);
		hitSound = this.itemTier == ItemTier.WOOD ? Sounds.BLUNT_HIT : Sounds.BLADE_HIT;
		
		if (swordAttackMotion == null) {
			swordAttackMotion = new ArrayList<StaticAnimation> ();
			swordAttackMotion.add(Animations.SWORD_AUTO_1);
			swordAttackMotion.add(Animations.SWORD_AUTO_2);
			swordAttackMotion.add(Animations.SWORD_AUTO_3);
			swordAttackMotion.add(Animations.SWORD_DASH);
			dualSwordAttackMotion = new ArrayList<StaticAnimation> ();
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_1);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_2);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_3);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_DASH);
		}
	}
	
	@Override
	protected void registerAttribute() {
		int i = itemTier.getHarvestLevel();
		
		oneHandedStyleDamageAttribute.put(ModAttributes.HIT_AT_ONCE, ModAttributes.getHitAtOnceModifier(1));
		oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i));
	}
	
	@Override
	public boolean hasSpecialAttack(PlayerData<?> playerdata) {
		return itemTier.getHarvestLevel() == 0 ? false : true;
	}
	
	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		CapabilityItem item = playerdata.getHeldItemCapability(Hand.OFF_HAND);
		if(item != null && item.weaponCategory == WeaponCategory.SWORD) {
			return Skills.DANCING_EDGE;
		} else {
			return Skills.SWEEPING_EDGE;
		}
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		if(playerdata.getOriginalEntity().getHeldItemOffhand().getItem() instanceof SwordItem) {
			return dualSwordAttackMotion;
		} else {
			return swordAttackMotion;
		}
	}
	
	@Override
	public SoundEvent getHitSound() {
		return hitSound;
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.sword;
	}

	@Override
	public boolean canCompatibleWith(ItemStack item) {
		return super.canCompatibleWith(item) || item.getItem() instanceof SwordItem;
	}
}