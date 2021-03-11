package maninhouse.epicfight.entity.ai;

import java.util.List;

import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.mob.BipedMobData;
import net.minecraft.entity.MobEntity;

public class SuccessivePatternGoal extends AttackPatternGoal
{
	public SuccessivePatternGoal(BipedMobData<?> mobdata, MobEntity attacker, double patternMinRange, boolean affectHorizon,
			double patternMaxRange, boolean successive, List<AttackAnimation> pattern)
	{
		super(mobdata, attacker, patternMinRange, patternMaxRange, affectHorizon, pattern);
	}
	
	@Override
	protected boolean canExecuteAttack()
    {
    	return !mobdata.isInaction() || mobdata.getEntityState().canAct();
    }
	
	@Override
    public void resetTask()
    {
		this.patternCounter = 0;
    }
}