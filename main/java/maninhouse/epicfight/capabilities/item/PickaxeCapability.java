package maninhouse.epicfight.capabilities.item;

import java.util.List;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.physics.Collider;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class PickaxeCapability extends MaterialItemCapability {
	public PickaxeCapability(Item item) {
		super(item, WeaponCategory.PICKAXE);
	}
	
	@Override
	protected void registerAttribute() {
		oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D));
		oneHandedStyleDamageAttribute.put(ModAttributes.IGNORE_DEFENCE, ModAttributes.getIgnoreDefenceModifier(6.0D * this.itemTier.getHarvestLevel()));
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return AxeCapability.axeAttackMotions;
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