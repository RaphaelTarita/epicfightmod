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

public class HoeCapability extends MaterialItemCapability {
	public HoeCapability(Item item) {
		super(item, WeaponCategory.HOE);
	}
	
	@Override
	protected void registerAttribute() {
		oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(-0.4D + 0.1D * this.itemTier.getHarvestLevel()));
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return toolAttackMotion;
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