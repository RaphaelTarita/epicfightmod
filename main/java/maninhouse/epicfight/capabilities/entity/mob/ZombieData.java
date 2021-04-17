package maninhouse.epicfight.capabilities.entity.mob;

import io.netty.buffer.ByteBuf;
import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.entity.DataKeys;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSReqSpawnInfo;
import maninhouse.epicfight.network.server.STCMobInitialSetting;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class ZombieData<T extends ZombieEntity> extends BipedMobData<T> {
	public ZombieData() {
		super(Faction.UNDEAD);
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		if (!this.isRemote()) {
			if (!this.orgEntity.canPickUpLoot()) {
				this.orgEntity.setCanPickUpLoot(isArmed());
			}
			
			if (this.orgEntity.isChild() && this.orgEntity.getRidingEntity() instanceof ChickenEntity) {
				if(this.orgEntity.getHeldItemMainhand().getItem() == Items.AIR) {
					this.orgEntity.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));
				}
			}
		} else {
			ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ZOMBIE_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public STCMobInitialSetting sendInitialInformationToClient() {
		STCMobInitialSetting packet = new STCMobInitialSetting(this.orgEntity.getEntityId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.orgEntity.canPickUpLoot());
        
		return packet;
	}
	
	@Override
	public void clientInitialSettings(ByteBuf buf) {
		AnimatorClient animator = this.getClientAnimator();
		
		if (buf.readBoolean()) {
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
			animator.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		}
	}
	
	@Override
	public void setAIAsUnarmed() {
		orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false, Animations.ZOMBIE_CHASE, Animations.ZOMBIE_WALK, !orgEntity.isChild()));
		orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.75D, true, MobAttackPatterns.ZOMBIE_NORAML));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}
}