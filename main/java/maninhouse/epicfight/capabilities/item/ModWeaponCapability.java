package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.skill.Skill;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public class ModWeaponCapability extends CapabilityItem
{
	protected final Function<PlayerData<?>, Skill> specialAttackGetter;
	protected final Skill weaponGimmick;
	protected final SoundEvent smashingSound;
	protected final SoundEvent hitSound;
	protected final Collider weaponCollider;
	protected final boolean twoHanded;
	protected final boolean mainHandOnly;
	protected List<StaticAnimation> autoAttackMotions;
	protected List<StaticAnimation> autoAttackTwohandMotions;
	protected List<StaticAnimation> mountAttackMotion;
	protected Map<LivingMotion, StaticAnimation> livingMotionChangers;
	
	public ModWeaponCapability(WeaponCategory category, Function<PlayerData<?>, Skill> specialAttack, Skill weaponGimmick, SoundEvent smash, SoundEvent hit,
			Collider weaponCollider, double armorIgnorance, double impact, int hitEnemiesAtOnce, boolean twoHanded, boolean mainHandOnly)
	{
		super(category);
		this.specialAttackGetter = specialAttack;
		this.weaponGimmick = weaponGimmick;
		this.smashingSound = smash;
		this.hitSound = hit;
		this.twoHanded = twoHanded;
		this.mainHandOnly = mainHandOnly;
		this.setOneHandStyleAttribute(armorIgnorance, impact, hitEnemiesAtOnce);
		this.weaponCollider = weaponCollider;
	}
	
	public void addLivingMotionChanger(LivingMotion livingMotion, StaticAnimation animation)
	{
		if(livingMotionChangers == null)
			livingMotionChangers = new HashMap<LivingMotion, StaticAnimation> ();
		
		livingMotionChangers.put(livingMotion, animation);
	}
	
	public void addAutoAttackCombos(StaticAnimation animation)
	{
		if(autoAttackMotions == null)
			autoAttackMotions = new ArrayList<StaticAnimation> ();
		
		autoAttackMotions.add(animation);
	}
	
	public void addTwohandAutoAttackCombos(StaticAnimation animation)
	{
		if(autoAttackTwohandMotions == null)
			autoAttackTwohandMotions = new ArrayList<StaticAnimation> ();
		
		autoAttackTwohandMotions.add(animation);
	}
	
	public void addMountAttackCombos(StaticAnimation animation)
	{
		if(mountAttackMotion == null)
			mountAttackMotion = new ArrayList<StaticAnimation> ();
		
		mountAttackMotion.add(animation);
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata)
	{
		ItemStack offHandItem = playerdata.getOriginalEntity().getHeldItemOffhand();
		
		return (autoAttackMotions != null || autoAttackTwohandMotions != null) ? (this.twoHanded || (!this.canUsedOffhand() && offHandItem.isEmpty())) ?
				autoAttackTwohandMotions : autoAttackMotions : super.getAutoAttckMotion(playerdata);
	}
	
	@Override
	public List<StaticAnimation> getMountAttackMotion()
	{
		return this.mountAttackMotion;
	}
	
	@Override
	public boolean hasSpecialAttack(PlayerData<?> playerdata)
	{
		return this.specialAttackGetter.apply(playerdata) != null;
	}
	
	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata)
	{
		return this.specialAttackGetter.apply(playerdata);
	}
	
	@Override
	public Skill getPassiveSkill()
	{
		return this.weaponGimmick;
	}
	
	@Override
	public SoundEvent getSmashingSound()
	{
		return this.smashingSound;
	}
	
	@Override
	public SoundEvent getHitSound()
	{
		return this.hitSound;
	}
	
	@Override
	public HitParticleType getHitParticle()
	{
		return Particles.HIT_CUT.get();
	}
	
	@Override
	public Collider getWeaponCollider()
	{
		return this.weaponCollider != null ? weaponCollider : super.getWeaponCollider();
	}
	
	@Override
	public boolean canUsedOffhand()
	{
		return this.twoHanded ? false : !this.mainHandOnly;
	}
	
	public void setOneHandStyleAttribute(double armorIgnorance, double impact, int hitEnemiesAtOnce)
	{
		if (armorIgnorance > 0.0D) {
			this.oneHandedStyleDamageAttribute.put(ModAttributes.IGNORE_DEFENCE, ModAttributes.getIgnoreDefenceModifier(armorIgnorance));
		}
		if (impact != 0.0D) {
			this.oneHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact));
		}
		if (hitEnemiesAtOnce > 0) {
			this.oneHandedStyleDamageAttribute.put(ModAttributes.HIT_AT_ONCE, ModAttributes.getHitAtOnceModifier(hitEnemiesAtOnce));
		}
	}
	
	public void setTwoHandStyleAttribute(double armorIgnorance, double impact, int hitEnemiesAtOnce)
	{
		if(twoHandedStyleDamageAttribute == null)
			this.twoHandedStyleDamageAttribute = new HashMap<Supplier<Attribute>, AttributeModifier>();
		
		if (armorIgnorance > 0.0D) {
			this.twoHandedStyleDamageAttribute.put(ModAttributes.IGNORE_DEFENCE, ModAttributes.getIgnoreDefenceModifier(armorIgnorance));
		}
		if (impact != 0.0D) {
			this.twoHandedStyleDamageAttribute.put(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact));
		}
		if (hitEnemiesAtOnce > 0) {
			this.twoHandedStyleDamageAttribute.put(ModAttributes.HIT_AT_ONCE, ModAttributes.getHitAtOnceModifier(hitEnemiesAtOnce));
		}
	}
	
	@Override
	public boolean isTwoHanded()
	{
		return this.twoHanded;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player)
	{
		return this.livingMotionChangers;
	}
}