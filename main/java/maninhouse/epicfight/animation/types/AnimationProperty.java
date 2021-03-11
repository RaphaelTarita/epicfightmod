package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.DamageType;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

public class AnimationProperty<T>
{
	public static final AnimationProperty<Integer> HIT_AT_ONCE = new AnimationProperty<Integer> ();
	public static final AnimationProperty<Float> DAMAGE_MULTIPLIER = new AnimationProperty<Float> ();
	public static final AnimationProperty<Float> DAMAGE_ADDER = new AnimationProperty<Float> ();
	public static final AnimationProperty<Float> ARMOR_NEGATION = new AnimationProperty<Float> ();
	public static final AnimationProperty<Float> IMPACT = new AnimationProperty<Float> ();
	public static final AnimationProperty<DamageType> DAMAGE_TYPE = new AnimationProperty<DamageType> ();
	public static final AnimationProperty<StunType> STUN_TYPE = new AnimationProperty<StunType> ();
	public static final AnimationProperty<SoundEvent> SWING_SOUND = new AnimationProperty<SoundEvent> ();
	public static final AnimationProperty<SoundEvent> HIT_SOUND = new AnimationProperty<SoundEvent> ();
	public static final AnimationProperty<Boolean> LOCK_ROTATION = new AnimationProperty<Boolean> ();
	public static final AnimationProperty<RegistryObject<HitParticleType>> PARTICLE = new AnimationProperty<RegistryObject<HitParticleType>> ();
}