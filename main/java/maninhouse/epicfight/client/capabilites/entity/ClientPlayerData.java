package maninhouse.epicfight.client.capabilites.entity;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSPlayAnimation;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.skill.SkillSlot;
import maninhouse.epicfight.utils.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerData extends RemoteClientPlayerData<ClientPlayerEntity> {
	private LivingEntity rayTarget;
	
	@Override
	public void onEntityConstructed(ClientPlayerEntity entity) {
		super.onEntityConstructed(entity);
		ClientEngine.INSTANCE.setPlayerData(this);
		ClientEngine.INSTANCE.inputController.setGamePlayer(this);
	}
	
	@Override
	public void updateMotion() {
		super.updateMotion();

		if (!this.getClientAnimator().prevAiming()) {
			if (this.currentMixMotion == LivingMotion.AIMING) {
				this.orgEntity.getItemInUseCount();
				ClientEngine.INSTANCE.renderEngine.zoomIn();
			}
		}
	}
	
	@Override
	public void updateOnClient() {
		super.updateOnClient();
		RayTraceResult rayResult = Minecraft.getInstance().objectMouseOver;

		if (rayResult.getType() == RayTraceResult.Type.ENTITY) {
			Entity hit = ((EntityRayTraceResult) rayResult).getEntity();
			if (hit instanceof LivingEntity)
				this.rayTarget = (LivingEntity) hit;
		}

		if (this.rayTarget != null) {
			if(!this.rayTarget.isAlive()) {
				this.rayTarget = null;
			} else if(this.getOriginalEntity().getDistanceSq(this.rayTarget) > 64.0D) {
				this.rayTarget = null;
			} else if(MathUtils.getAngleBetween(this.getOriginalEntity(), this.rayTarget) > 1.5707963267948966D) {
				this.rayTarget = null;
			}
		}
	}
	
	@Override
	protected void playReboundAnimation() {
		this.getClientAnimator().playReboundAnimation();
		ClientEngine.INSTANCE.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime) {
		ModNetworkManager.sendToServer(new CTSPlayAnimation(id, modifyTime, false, true));
	}
	
	@Override
	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		super.onHeldItemChange(mainHandCap, offHandCap);

		if (mainHandCap != null) {
			mainHandCap.onHeld(this);
		} else {
			this.getSkill(SkillSlot.WEAPON_GIMMICK).setSkill(null);
		}
	}
	
	@Override
	public void aboutToDeath() {
		;
	}
	
	public void initFromOldOne(ClientPlayerData old) {
		this.skills = old.skills;

		for (SkillContainer skill : this.skills) {
			skill.setExecuter(this);
		}
		
		this.setStunArmor(old.getStunArmor());
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		return this.rayTarget;
	}
	
	@Override
	public boolean isFirstPerson() {
		return Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON;
	}
}