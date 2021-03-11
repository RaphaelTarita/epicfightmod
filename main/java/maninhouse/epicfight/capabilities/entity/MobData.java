package maninhouse.epicfight.capabilities.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.mob.Faction;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.entity.ai.ArcherGoal;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.RangeAttackMobGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.network.server.STCMobInitialSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.inventory.EquipmentSlotType;

public abstract class MobData<T extends MobEntity> extends LivingData<T> {
	protected final Faction mobFaction;
	
	public MobData() {
		this.mobFaction = Faction.NATURAL;
	}
	
	public MobData(Faction faction) {
		this.mobFaction = faction;
	}
	
	@Override
	public boolean onEntityJoinWorld(T entityIn) {
		if(super.onEntityJoinWorld(entityIn)) {
			if(!this.isRemote() && !this.orgEntity.isAIDisabled()) {
				initAI();
			}
			return true;
		} else {
			return false;
		}
	}
	
	protected void initAI() {
		resetCombatAI();
	}

	protected void resetCombatAI() {
		Set<PrioritizedGoal> goals = this.orgEntity.goalSelector.goals;
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		List<Goal> toRemove = Lists.<Goal>newArrayList();
		
        while (iterator.hasNext())
        {
        	PrioritizedGoal goal = iterator.next();
            Goal inner = goal.getGoal();
            
            if (inner instanceof MeleeAttackGoal || inner instanceof RangedBowAttackGoal  || inner instanceof ArcherGoal || inner instanceof ChasingGoal
            		|| inner instanceof RangedAttackGoal || inner instanceof RangeAttackMobGoal || inner instanceof AttackPatternGoal || inner instanceof RangedCrossbowAttackGoal)
            {
            	toRemove.add(inner);
            }
        }
        
        for(Goal AI : toRemove)
        {
        	orgEntity.goalSelector.removeGoal(AI);
        }
	}
	
	public STCMobInitialSetting sendInitialInformationToClient()
	{
		return null;
	}
	
	public void clientInitialSettings(ByteBuf buf)
	{
		
	}
	
	@Override
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		if(this.orgEntity.getAttributeManager().hasAttributeInstance(ModAttributes.MAX_STUN_ARMOR.get())) {
			if(fromCap != null) {
				this.orgEntity.getAttributeManager().removeModifiers(fromCap.getAttributeModifiers(slotType, this));
			}
			if(toCap != null) {
				this.orgEntity.getAttributeManager().reapplyModifiers(toCap.getAttributeModifiers(slotType, this));
			}
			
			this.setStunArmor((float)this.orgEntity.getAttributeValue(ModAttributes.MAX_STUN_ARMOR.get()));
		}
	}
	
	@Override
	public boolean isTeam(Entity entityIn)
	{
		CapabilityEntity<?> cap = entityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if(cap != null && cap instanceof MobData)
			if(((MobData<?>)cap).mobFaction.equals(this.mobFaction))
			{
				Optional<LivingEntity> opt = Optional.ofNullable(this.getAttackTarget());
				return opt.map((attackTarget)->!attackTarget.isEntityEqual(entityIn)).orElse(true);
			}
		
		return super.isTeam(entityIn);
	}
	
	@Override
	public LivingEntity getAttackTarget()
	{
		return this.orgEntity.getAttackTarget();
	}
}