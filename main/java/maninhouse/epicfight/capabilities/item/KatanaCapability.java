package maninhouse.epicfight.capabilities.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.skill.SkillSlot;

public class KatanaCapability extends ModWeaponCapability {
	private Map<LivingMotion, StaticAnimation> sheathedMotions;
	
	public KatanaCapability() {
		super(WeaponCategory.KATANA, (playerdata)->Skills.FATAL_DRAW, Skills.KATANA_GIMMICK, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.katana, 0.0D, 0.6D, 1, true, true);
		this.setTwoHandStyleAttribute(0.0D, 0.6D, 1);
		this.addAutoAttackCombos(Animations.KATANA_SHEATHING_AUTO);
		this.addAutoAttackCombos(Animations.KATANA_SHEATHING_DASH);
		this.addTwohandAutoAttackCombos(Animations.KATANA_AUTO_1);
		this.addTwohandAutoAttackCombos(Animations.KATANA_AUTO_2);
		this.addTwohandAutoAttackCombos(Animations.KATANA_AUTO_3);
		this.addTwohandAutoAttackCombos(Animations.SWORD_DASH);
		this.addMountAttackCombos(Animations.SWORD_MOUNT_ATTACK);
    	this.addLivingMotionChanger(LivingMotion.IDLE, Animations.BIPED_IDLE_UNSHEATHING);
    	this.addLivingMotionChanger(LivingMotion.WALKING, Animations.BIPED_WALK_UNSHEATHING);
    	this.addLivingMotionChanger(LivingMotion.RUNNING, Animations.BIPED_RUN_UNSHEATHING);
    	this.sheathedMotions = new HashMap<LivingMotion, StaticAnimation> ();
    	this.sheathedMotions.put(LivingMotion.IDLE, Animations.BIPED_IDLE_SHEATHING);
    	this.sheathedMotions.put(LivingMotion.WALKING, Animations.BIPED_WALK_SHEATHING);
    	this.sheathedMotions.put(LivingMotion.RUNNING, Animations.BIPED_RUN_SHEATHING);
    	this.sheathedMotions.put(LivingMotion.JUMPING, Animations.BIPED_JUMP_SHEATHING);
    	this.sheathedMotions.put(LivingMotion.KNEELING, Animations.BIPED_KNEEL_SHEATHING);
    	this.sheathedMotions.put(LivingMotion.SNEAKING, Animations.BIPED_SNEAK_SHEATHING);
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		if(playerdata.getSkill(SkillSlot.WEAPON_GIMMICK).getVariableNBT().getBoolean("sheath")) {
			return autoAttackMotions;
		} else {
			return super.autoAttackTwohandMotions;
		}
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> player) {
		if(player.getSkill(SkillSlot.WEAPON_GIMMICK).getVariableNBT().getBoolean("sheath")) {
			return this.sheathedMotions;
		} else {
			return super.getLivingMotionChanges(player);
		}
	}
	
	@Override
	public boolean canUseOnMount() {
		return true;
	}
}