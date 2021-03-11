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
import net.minecraft.item.ItemTier;
import net.minecraft.util.SoundEvent;

public class AxeCapability extends MaterialItemCapability {
	protected static List<StaticAnimation> axeAttackMotions = new ArrayList<StaticAnimation> ();
	private Skill specialAttack;
	
	static {
		axeAttackMotions = new ArrayList<StaticAnimation> ();
		axeAttackMotions.add(Animations.AXE_AUTO1);
		axeAttackMotions.add(Animations.AXE_AUTO2);
		axeAttackMotions.add(Animations.AXE_DASH);
	}
	
	public AxeCapability(Item item) {
		super(item, WeaponCategory.AXE);
		
		specialAttack = itemTier == ItemTier.WOOD ? null : Skills.GUILLOTINE_AXE;
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return axeAttackMotions;
	}
	
	@Override
	public boolean hasSpecialAttack(PlayerData<?> playerdata) {
		return itemTier.getHarvestLevel() == 0 ? false : true;
	}

	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		return specialAttack;
	}
	
	@Override
	protected void registerAttribute() {
		int i = itemTier.getHarvestLevel();
		if(i != 0) {
			oneHandedStyleDamageAttribute.put(ModAttributes.IGNORE_DEFENCE, ModAttributes.getIgnoreDefenceModifier(10.0D * i));
		}
		oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(2.0D + 0.5D * i));
	}
	
	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLADE_HIT;
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.tools;
	}
}