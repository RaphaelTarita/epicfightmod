package maninhouse.epicfight.skill;

import java.util.List;
import java.util.UUID;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.entity.event.EntityEventListener.EventType;
import maninhouse.epicfight.entity.event.PlayerEvent;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class LethalSlicingSkill extends SpecialAttackSkill {
	private static final UUID EVENT_UUID = UUID.fromString("bfa79c04-97a5-11eb-a8b3-0242ac130003");
	private StaticAnimation elbow;
	private StaticAnimation swing;
	private StaticAnimation doubleSwing;
	
	public LethalSlicingSkill(float restriction, String skillName) {
		super(restriction, skillName);
		this.elbow = Animations.LETHAL_SLICING;
		this.swing = Animations.LETHAL_SLICING_ONCE;
		this.doubleSwing = Animations.LETHAL_SLICING_TWICE;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.executer.getEventListener().addEventListener(EventType.ON_ATTACK_END_EVENT, PlayerEvent.makeEvent(EVENT_UUID, (player, args)->{
			if (((int)args[1]) == Animations.LETHAL_SLICING.getId()) {
				int hitEnemies = (int)args[0];
				if (hitEnemies == 1) {
					player.reserverAnimationSynchronize(this.swing);
				} else if (hitEnemies > 1) {
					player.reserverAnimationSynchronize(this.doubleSwing);
				}
			}
			return false;
		}));
	}
	
	@Override
	public void onDeleted(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ON_ATTACK_END_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(this.elbow, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Elbow:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Each Strike:");
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		AttackAnimation _elbow = ((AttackAnimation)this.elbow);
		AttackAnimation _swing = ((AttackAnimation)this.swing);
		AttackAnimation _doubleSwing = ((AttackAnimation)this.doubleSwing);
		_elbow.phases[0].addProperties(this.properties.get(0).entrySet());
		_swing.phases[0].addProperties(this.properties.get(1).entrySet());
		_doubleSwing.phases[0].addProperties(this.properties.get(1).entrySet());
		_doubleSwing.phases[1].addProperties(this.properties.get(1).entrySet());
		
		return this;
	}
}