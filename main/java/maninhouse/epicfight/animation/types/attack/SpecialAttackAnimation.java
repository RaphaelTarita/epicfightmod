package maninhouse.epicfight.animation.types.attack;

import javax.annotation.Nullable;

import maninhouse.epicfight.animation.types.AnimationProperty;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.physics.Collider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;

public class SpecialAttackAnimation extends AttackAnimation {
	public SpecialAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider,
			String index, String path)
	{
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public SpecialAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand, @Nullable Collider collider,
			String index, String path)
	{
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public SpecialAttackAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases)
	{
		super(id, convertTime, affectY, path, phases);
	}
	
	@Override
	protected float getDamageAmount(LivingData<?> entitydata, Entity target, Hand hand)
	{
		float multiplier = this.getProperty(AnimationProperty.DAMAGE_MULTIPLIER).orElse(1.0F);
		float adder = this.getProperty(AnimationProperty.DAMAGE_ADDER).orElse(0.0F);
		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SWEEPING, entitydata.getOriginalEntity());
	    multiplier += (i > 0) ? (float)i / (float)(i + 1.0F) : 0.0F;
	    
		return entitydata.getDamageToEntity(target, hand) * multiplier + adder;
	}
}