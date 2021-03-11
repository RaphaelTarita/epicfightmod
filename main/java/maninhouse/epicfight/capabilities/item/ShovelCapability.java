package maninhouse.epicfight.capabilities.item;

import java.util.List;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class ShovelCapability extends MaterialItemCapability {
	public ShovelCapability(Item item) {
		super(item, WeaponCategory.SHOVEL);
	}
	
	@Override
	protected void registerAttribute() {
		double impact = itemTier.getHarvestLevel() * 0.5D + 1.5D;
		oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact));
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return AxeCapability.axeAttackMotions;
	}

	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLUNT_HIT;
	}

	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLUNT.get();
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.tools;
	}
}