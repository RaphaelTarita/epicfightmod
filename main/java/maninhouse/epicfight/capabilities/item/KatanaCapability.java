package maninhouse.epicfight.capabilities.item;

import java.util.HashMap;
import java.util.Map;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.skill.SkillSlot;

public class KatanaCapability extends ModWeaponCapability {
	private Map<LivingMotion, StaticAnimation> sheathedMotions;
	
	public KatanaCapability() {
		super(WeaponCategory.KATANA, (playerdata)->WieldStyle.TWO_HAND, Skills.KATANA_GIMMICK, Sounds.WHOOSH, Sounds.BLADE_HIT, Colliders.katana, HandProperty.TWO_HANDED);
		this.addStyleAttributeSimple(WieldStyle.TWO_HAND, 0.0D, 0.6D, 1);
		this.addStyleCombo(WieldStyle.SHEATH, Animations.KATANA_SHEATHING_AUTO, Animations.KATANA_SHEATHING_DASH);
		this.addStyleCombo(WieldStyle.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH);
		this.addStyleCombo(WieldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK);
		this.addStyleSpecialAttack(WieldStyle.SHEATH, Skills.FATAL_DRAW);
		this.addStyleSpecialAttack(WieldStyle.TWO_HAND, Skills.FATAL_DRAW);
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
	public WieldStyle getStyle(LivingData<?> entitydata) {
		if (entitydata instanceof PlayerData) {
			PlayerData<?> playerdata = (PlayerData<?>)entitydata;
			if (playerdata.getSkill(SkillSlot.WEAPON_GIMMICK).getVariableNBT().getBoolean("sheath")) {
				return WieldStyle.SHEATH;
			} else {
				return WieldStyle.TWO_HAND;
			}
		} else {
			return WieldStyle.TWO_HAND;
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