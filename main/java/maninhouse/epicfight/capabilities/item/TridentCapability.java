package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class TridentCapability extends RangedWeaponCapability
{
	private static List<StaticAnimation> attackMotion;
	
	public TridentCapability(Item item)
	{
		super(item, null, Animations.BIPED_JAVELIN_AIM, Animations.BIPED_JAVELIN_REBOUND);
		
		if(attackMotion == null)
		{
			attackMotion = new ArrayList<StaticAnimation> ();
			attackMotion.add(Animations.SPEAR_ONEHAND_AUTO);
			attackMotion.add(Animations.SPEAR_DASH);
		}
	}
	
	@Override
	protected void registerAttribute()
	{
		oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(2.25D));
	}
	
	@Override
	public SoundEvent getHitSound()
	{
		return Sounds.BLADE_HIT;
	}
	
	@Override
	public HitParticleType getHitParticle()
	{
		return Particles.HIT_CUT.get();
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return Colliders.spearNarrow;
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata)
	{
		return attackMotion;
	}
	
	@Override
	public boolean canUsedOffhand()
	{
		return false;
	}
}