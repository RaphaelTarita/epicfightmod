package maninhouse.epicfight.capabilities.entity.player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCLivingMotionChange;
import maninhouse.epicfight.network.server.STCNotifyPlayerYawChanged;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import maninhouse.epicfight.network.server.STCSetSkillValue;
import maninhouse.epicfight.network.server.STCSetSkillValue.Target;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.skill.SkillSlot;
import maninhouse.epicfight.utils.game.Formulars;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ServerPlayerData extends PlayerData<ServerPlayerEntity>
{
	private Map<LivingMotion, StaticAnimation> livingMotionMap = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private List<LivingMotion> modifiedLivingMotions = Lists.<LivingMotion>newArrayList();
	
	private static final UUID WEIGHT_PANELTY_MODIFIIER = UUID.fromString("414fed9e-e5e3-11ea-adc1-0242ac120002");
	private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
	@Override
	public void gatherDamageDealt(IExtendedDamageSource source, float amount)
	{
		if(source.getSkillId() > Animations.BASIC_ATTACK_MIN && source.getSkillId() < Animations.BASIC_ATTACK_MAX)
		{
			SkillContainer container = this.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
			CapabilityItem itemCap = this.getHeldItemCapability(Hand.MAIN_HAND);
			
			if(itemCap != null && container.hasSkill(itemCap.getSpecialAttack(this)))
			{
				float value = container.getRemainCooldown() + amount;
				
				if(value > 0.0F)
				{
					this.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK).setCooldown(value);
					ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.COOLDOWN, SkillSlot.WEAPON_SPECIAL_ATTACK.getIndex(), value, false), orgEntity);
				}
			}
		}
	}
	
	@Override
	public boolean onEntityJoinWorld(ServerPlayerEntity entityIn) {
		if(super.onEntityJoinWorld(entityIn)) {
			livingMotionMap.put(LivingMotion.IDLE, Animations.BIPED_IDLE);
			livingMotionMap.put(LivingMotion.WALKING, Animations.BIPED_WALK);
			livingMotionMap.put(LivingMotion.RUNNING, Animations.BIPED_RUN);
			livingMotionMap.put(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
			livingMotionMap.put(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
			livingMotionMap.put(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
			livingMotionMap.put(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
			livingMotionMap.put(LivingMotion.FALL, Animations.BIPED_FALL);
			livingMotionMap.put(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
			livingMotionMap.put(LivingMotion.FLYING, Animations.BIPED_FLYING);
			livingMotionMap.put(LivingMotion.DEATH, Animations.BIPED_DEATH);
			
			for (Map.Entry<LivingMotion, StaticAnimation> entry : livingMotionMap.entrySet()) {
				defaultLivingAnimations.put(entry.getKey(), entry.getValue());
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void updateMotion()
	{
		;
	}
	
	public void onHeldItemChange(CapabilityItem toChange, ItemStack stack, Hand hand)
	{
		CapabilityItem mainHandCap = (hand == Hand.MAIN_HAND) ? toChange : this.getHeldItemCapability(Hand.MAIN_HAND);
		if(mainHandCap != null)
			mainHandCap.onHeld(this);
		else
			this.getSkill(SkillSlot.WEAPON_GIMMICK).setSkill(null);
		
		if(hand == Hand.MAIN_HAND)
		{
			this.orgEntity.getAttribute(Attributes.ATTACK_SPEED).removeModifier(WEIGHT_PANELTY_MODIFIIER);
			float weaponSpeed = (float) this.orgEntity.getAttribute(Attributes.ATTACK_SPEED).getBaseValue();
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
				weaponSpeed += attributeModifier.getAmount();
			
			this.orgEntity.getAttribute(Attributes.ATTACK_SPEED).applyNonPersistentModifier(new AttributeModifier(WEIGHT_PANELTY_MODIFIIER, "weight panelty modifier",
					Formulars.getAttackSpeedPanelty(this.getWeight(), weaponSpeed), Operation.ADDITION));
		}
		else
		{
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED.get()).removeModifier(WEIGHT_PANELTY_MODIFIIER);
			float weaponSpeed = (float) this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED.get()).getBaseValue();
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
				weaponSpeed += attributeModifier.getAmount();
			
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED.get()).applyNonPersistentModifier(new AttributeModifier(WEIGHT_PANELTY_MODIFIIER, "weight panelty modifier",
					Formulars.getAttackSpeedPanelty(this.getWeight(), weaponSpeed), Operation.ADDITION));
			
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_DAMAGE.get()).removeModifier(ATTACK_DAMAGE_MODIFIER);
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE))
				this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_DAMAGE.get()).applyNonPersistentModifier(attributeModifier);
		}
		
		this.modifiLivingMotions(mainHandCap);
	}
	
	@Override
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType)
	{
		ModifiableAttributeInstance mainhandAttackSpeed = this.orgEntity.getAttribute(Attributes.ATTACK_SPEED);
		ModifiableAttributeInstance offhandAttackSpeed = this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED.get());
		
		mainhandAttackSpeed.removeModifier(WEIGHT_PANELTY_MODIFIIER);
		float mainWeaponSpeed = (float) mainhandAttackSpeed.getBaseValue();
		for(AttributeModifier attributeModifier : this.getOriginalEntity().getHeldItemMainhand().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
			mainWeaponSpeed += (float)attributeModifier.getAmount();
		mainhandAttackSpeed.applyNonPersistentModifier(new AttributeModifier(WEIGHT_PANELTY_MODIFIIER, "weight panelty modifier",
				Formulars.getAttackSpeedPanelty(this.getWeight(), mainWeaponSpeed), Operation.ADDITION));
		
		offhandAttackSpeed.removeModifier(WEIGHT_PANELTY_MODIFIIER);
		float offWeaponSpeed = (float) offhandAttackSpeed.getBaseValue();
		for(AttributeModifier attributeModifier : this.getOriginalEntity().getHeldItemOffhand().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED))
			offWeaponSpeed += (float)attributeModifier.getAmount();
		offhandAttackSpeed.applyNonPersistentModifier(new AttributeModifier(WEIGHT_PANELTY_MODIFIIER, "weight panelty modifier",
				Formulars.getAttackSpeedPanelty(this.getWeight(), offWeaponSpeed), Operation.ADDITION));
	}
	
	public void modifiLivingMotions(CapabilityItem mainhand)
	{
		this.resetModifiedLivingMotions();
		
		if(mainhand != null)
		{
			Map<LivingMotion, StaticAnimation> motionChanger = mainhand.getLivingMotionChanges(this);
			if(motionChanger != null)
			{
				List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
				List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
				
				for(Map.Entry<LivingMotion, StaticAnimation> entry : motionChanger.entrySet())
				{
					this.addModifiedLivingMotion(entry.getKey(), entry.getValue());
					motions.add(entry.getKey());
					animations.add(entry.getValue());
				}
				
				LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
				StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
				STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getEntityId(), motionChanger.size());
				msg.setMotions(motionarr);
				msg.setAnimations(animationarr);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
				return;
			}
		}
		
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getEntityId(), 0);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
	}
	
	private void addModifiedLivingMotion(LivingMotion motion, StaticAnimation animation) {
		if(animation != null) {
			if (!this.modifiedLivingMotions.contains(motion)) {
				this.modifiedLivingMotions.add(motion);
			}
			
			this.livingMotionMap.put(motion, animation);
		}
	}
	
	private void resetModifiedLivingMotions() {
		for(LivingMotion livingMotion : modifiedLivingMotions) {
			this.livingMotionMap.put(livingMotion, defaultLivingAnimations.get(livingMotion));
		}
		
		modifiedLivingMotions.clear();
	}
	
	public void modifiLivingMotionToAll(STCLivingMotionChange packet)
	{
		LivingMotion[] motions = packet.getMotions();
		StaticAnimation[] animations = packet.getAnimations();
		
		for(int i = 0; i < motions.length; i++) {
			this.addModifiedLivingMotion(motions[i], animations[i]);
		}
		
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(packet, this.orgEntity);
	}
	
	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingMotionEntrySet()
	{
		return this.livingMotionMap.entrySet();
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime)
	{
		super.playAnimationSynchronize(id, modifyTime);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}
	
	@Override
	public void changeYaw(float amount)
	{
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(this.orgEntity.getEntityId(), yaw), this.orgEntity);
		ModNetworkManager.sendToPlayer(new STCNotifyPlayerYawChanged(this.orgEntity.getEntityId(), yaw), this.orgEntity);
	}
	
	@Override
	public ServerPlayerEntity getOriginalEntity()
	{
		return orgEntity;
	}
	
	@Override
	public void aboutToDeath()
	{
		;
	}
}