package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.Difficulty;

public class CaveSpiderData extends SpiderData<CaveSpiderEntity>
{
	@Override
	protected void initAI()
	{
		super.initAI();
        
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.0D, true, MobAttackPatterns.SPIDER_PATTERN));
        orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
	}
	
	@Override
	public boolean hurtEntity(Entity hitTarget, IExtendedDamageSource source, float amount)
	{
		boolean succed = super.hurtEntity(hitTarget, source, amount);
		
		if(succed && hitTarget instanceof LivingEntity)
        {
			int i = 0;
            if (this.orgEntity.world.getDifficulty() == Difficulty.NORMAL)
                i = 7;
            else if (this.orgEntity.world.getDifficulty() == Difficulty.HARD)
                i = 15;

            if (i > 0)
                ((LivingEntity)hitTarget).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
        }
		
		return succed;
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks)
	{
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);
		return VisibleMatrix4f.scale(new Vec3f(0.7F, 0.7F, 0.7F), mat, mat);
	}
}