package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class AxeCapability extends MaterialItemCapability {
	protected static List<StaticAnimation> axeAttackMotions = new ArrayList<StaticAnimation> ();
	
	static {
		axeAttackMotions = new ArrayList<StaticAnimation> ();
		axeAttackMotions.add(Animations.AXE_AUTO1);
		axeAttackMotions.add(Animations.AXE_AUTO2);
		axeAttackMotions.add(Animations.AXE_DASH);
	}
	
	public AxeCapability(Item item) {
		super(item, WeaponCategory.AXE);
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return axeAttackMotions;
	}

	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		return Skills.GUILLOTINE_AXE;
	}
	
	@Override
	protected void registerAttribute() {
		int i = this.itemTier.getHarvestLevel();
		
		if(i != 0) {
			this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.ARMOR_NEGATION, ModAttributes.getArmorNegationModifier(10.0D * i)));
		}
		
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.7D + 0.3D * i)));
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLADE.get();
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