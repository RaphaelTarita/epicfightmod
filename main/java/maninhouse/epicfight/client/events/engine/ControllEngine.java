package maninhouse.epicfight.client.events.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.client.input.ModKeys;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSPlayAnimation;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.skill.SkillSlot;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class ControllEngine {
	private Map<KeyBinding, BiConsumer<Integer, Integer>> keyFunctionMap;
	private GLFWCursorPosCallbackI callback = (handle, x, y) -> {tracingMouseX = x; tracingMouseY = y;};
	private ClientPlayerEntity player;
	private ClientPlayerData playerdata;
	private KeyBindingMap keyHash;
	private double tracingMouseX;
	private double tracingMouseY;
	private int comboHoldCounter;
	private int comboCounter;
	private int mouseLeftPressCounter = 0;
	private int sneakPressCounter = 0;
	private int reservedSkill;
	private int skillReserveCounter;
	private boolean sneakPressToggle = false;
	private boolean mouseLeftPressToggle = false;
	private boolean lightPress;
	
	public GameSettings gameSettings;
	
	public ControllEngine() {
		Events.controllEngine = this;
		gameSettings = Minecraft.getInstance().gameSettings;
		keyFunctionMap = new HashMap<KeyBinding, BiConsumer<Integer, Integer>>();
		keyFunctionMap.put(gameSettings.keyBindAttack, this::attackKeyPressed);
		keyFunctionMap.put(gameSettings.keyBindSwapHands, this::swapHandKeyPressed);
		keyFunctionMap.put(ModKeys.SWITCH_MODE, this::switchModeKeyPressed);
		keyFunctionMap.put(ModKeys.DODGE, this::sneakKeyPressed);
		
		try {
			keyHash = (KeyBindingMap) ObfuscationReflectionHelper.findField(KeyBinding.class, "field_74514_b").get(null);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void setGamePlayer(ClientPlayerData playerdata) {
		this.comboCounter = 0;
		this.mouseLeftPressCounter = 0;
		this.mouseLeftPressToggle = false;
		this.sneakPressCounter = 0;
		this.sneakPressToggle = false;
		this.lightPress = false;
		this.player = playerdata.getOriginalEntity();
		this.playerdata = playerdata;
	}
	
	public boolean playerCanMove(EntityState playerState) {
		return !playerState.isMovementLocked() || this.player.isRidingHorse();
	}

	public boolean playerCanRotate(EntityState playerState) {
		return !playerState.isCameraRotationLocked() || this.player.isRidingHorse();
	}

	public boolean playerCanAct(EntityState playerState) {
		return !this.player.isSpectator() && !(this.player.isElytraFlying() || playerdata.currentMotion == LivingMotion.FALL || playerState.isMovementLocked());
	}

	public boolean playerCanDodging(EntityState playerState) {
		return !this.player.isSpectator() && !(this.player.isElytraFlying() || playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}

	public boolean playerCanExecuteSkill(EntityState playerState) {
		return !this.player.isSpectator() && !(this.player.isElytraFlying() || playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	private void attackKeyPressed(int key, int action) {
		if (action == 1) {
			if (ClientEngine.INSTANCE.isBattleMode()) {
				this.setKeyBind(gameSettings.keyBindAttack, false);
				while(gameSettings.keyBindAttack.isPressed()) {
				}

				if (player.getItemInUseCount() == 0) {
					if (!mouseLeftPressToggle) {
						mouseLeftPressToggle = true;
					}
				}
			}
		}

		if (player.getCooledAttackStrength(0) < 0.9F) {
			while(gameSettings.keyBindAttack.isPressed()) {
			}
		}
	}
	
	private void sneakKeyPressed(int key, int action) {
		if (action == 1) {
			if (key == gameSettings.keyBindSneak.getKey().getKeyCode()) {
				if (player.getRidingEntity() == null && ClientEngine.INSTANCE.isBattleMode()) {
					if (!sneakPressToggle)
						sneakPressToggle = true;
				}
			} else {
				SkillContainer skill = this.playerdata.getSkill(SkillSlot.DODGE);
				if (skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata))
					skill.execute(this.playerdata);
			}
		}
	}
	
	private void swapHandKeyPressed(int key, int action) {
		CapabilityItem cap = this.playerdata.getHeldItemCapability(Hand.MAIN_HAND);

		if (this.playerdata.isInaction() || (cap != null && !cap.canUsedInOffhand())) {
			while (gameSettings.keyBindSwapHands.isPressed()) {
			}
			this.setKeyBind(gameSettings.keyBindSwapHands, false);
		}
	}
	
	private void switchModeKeyPressed(int key, int action) {
		if (action == 1) {
			ClientEngine.INSTANCE.toggleActingMode();
		}
	}
	
	public void tick() {
		if (this.playerdata == null)
			return;

		EntityState playerState = this.playerdata.getEntityState();

		if (this.mouseLeftPressToggle) {
			if (!this.isKeyDown(gameSettings.keyBindAttack)) {
				this.lightPress = true;
				this.mouseLeftPressToggle = false;
				this.mouseLeftPressCounter = 0;
			} else {
				if (this.mouseLeftPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
					if (this.playerCanExecuteSkill(playerState)) {
						CapabilityItem itemCap = playerdata.getHeldItemCapability(Hand.MAIN_HAND);
						if(itemCap != null) {
							this.playerdata.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK).execute(this.playerdata);
						}
					} else {
						if (!this.player.isSpectator()) {
							this.reserveSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
						}
					}
					
					this.mouseLeftPressToggle = false;
					this.mouseLeftPressCounter = 0;
					this.resetAttackCounter();
				} else {
					this.setKeyBind(this.gameSettings.keyBindAttack, false);
					this.mouseLeftPressCounter++;
				}
			}
		}
		
		if (this.lightPress) {
			if (this.playerCanAct(playerState)) {
				playAttackMotion(this.player.getHeldItemMainhand(), this.player.isSprinting());
				this.player.resetCooldown();
				this.lightPress = false;
			} else {
				if (this.player.isSpectator() || playerState.getLevel() < 2) {
					lightPress = false;
				}
			}
			
			this.mouseLeftPressToggle = false;
			this.mouseLeftPressCounter = 0;
		}

		if (this.sneakPressToggle) {
			if (!this.isKeyDown(this.gameSettings.keyBindSneak)) {
				SkillContainer skill = this.playerdata.getSkill(SkillSlot.DODGE);
				
				if(skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata)) {
					skill.execute(this.playerdata);
				} else {
					this.reserveSkill(SkillSlot.DODGE);
				}
				
				sneakPressToggle = false;
				sneakPressCounter = 0;
			} else {
				if (sneakPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
					sneakPressToggle = false;
					sneakPressCounter = 0;
				} else {
					sneakPressCounter++;
				}
			}
		}
		
		if (this.reservedSkill >= 0) {
			if (skillReserveCounter > 0) {
				SkillContainer skill = playerdata.getSkill(reservedSkill);
				skillReserveCounter--;
				if(skill.getContaining() != null && skill.canExecute(playerdata) && skill.getContaining().isExecutableState(this.playerdata)) {
					skill.execute(playerdata);
					this.reservedSkill = -1;
					this.skillReserveCounter = -1;
				}
			} else {
				this.reservedSkill = -1;
				this.skillReserveCounter = -1;
			}
		}
		
		if (this.comboHoldCounter > 0) {
			float f = player.getCooledAttackStrength(0);
			
			if (!playerState.isMovementLocked() && !playerState.isCameraRotationLocked() && f >= 1.0F) {
				--this.comboHoldCounter;
				
				if (comboHoldCounter == 0) {
					this.resetAttackCounter();
				}
			}
		}
		
		for (int i = 0; i < 9; ++i) {
			if (isKeyDown(gameSettings.keyBindsHotbar[i])) {
				if (playerdata.isInaction())
					gameSettings.keyBindsHotbar[i].isPressed();
			}
		}
		
		if (Minecraft.getInstance().isGamePaused()) {
			Minecraft.getInstance().mouseHelper.registerCallbacks(Minecraft.getInstance().getMainWindow().getHandle());
		}
	}
	
	private void playAttackMotion(ItemStack holdItem, boolean dashAttack) {
		CapabilityItem cap = holdItem.getCapability(ModCapabilities.CAPABILITY_ITEM, null).orElse(null);
		StaticAnimation attackMotion = null;
		
		if (player.getRidingEntity() != null) {
			if (player.isRidingHorse() && cap != null && cap.canUseOnMount()) {
				attackMotion = cap.getMountAttackMotion().get(comboCounter);
				comboCounter += 1;
				comboCounter %= cap.getMountAttackMotion().size();
			}
		} else {
			List<StaticAnimation> combo = null;
			
			if(combo == null) {
				combo = (cap != null) ? combo = cap.getAutoAttckMotion(this.playerdata) : CapabilityItem.getBasicAutoAttackMotion();
			}
			int comboSize = combo.size();
			if(dashAttack) {
				comboCounter = comboSize - 1;
			} else {
				comboCounter %= comboSize - 1;
			}
			
			attackMotion = combo.get(comboCounter);
			comboCounter = dashAttack ? 0 : comboCounter+1;
		}
		
		comboHoldCounter = 10;
		
		if(attackMotion != null) {
			this.playerdata.getAnimator().playAnimation(attackMotion, 0);
			ModNetworkManager.sendToServer(new CTSPlayAnimation(attackMotion, 0, false, false));
		}
	}
	
	private void reserveSkill(SkillSlot slot) {
		this.reservedSkill = slot.getIndex();
		this.skillReserveCounter = 8;
	}
	
	public boolean isKeyDown(KeyBinding key) {
		if(key.getKey().getType() == InputMappings.Type.KEYSYM) {
			return GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode()) > 0;
		} else if(key.getKey().getType() == InputMappings.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(Minecraft.getInstance().getMainWindow().getHandle(), key.getKey().getKeyCode()) > 0;
		} else {
			return false;
		}
	}
	
	public void setKeyBind(KeyBinding key, boolean setter) {
		KeyBinding.setKeyBindState(key.getKey(), setter);
	}
	
	public void resetAttackCounter() {
		comboCounter = 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
	public static class Events {
		static ControllEngine controllEngine;
		
		@SubscribeEvent
		public static void mouseEvent(MouseInputEvent event) {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().currentScreen == null) {
				Input input = InputMappings.Type.MOUSE.getOrMakeInput(event.getButton());
				for (KeyBinding keybinding : controllEngine.keyHash.lookupAll(input)) {
					if(controllEngine.keyFunctionMap.containsKey(keybinding)) {
						controllEngine.keyFunctionMap.get(keybinding).accept(event.getButton(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void mouseScrollEvent(MouseScrollEvent event) {
			if (Minecraft.getInstance().player != null && controllEngine.playerdata != null && controllEngine.playerdata.isInaction()) {
				if(Minecraft.getInstance().currentScreen == null) {
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void keyboardEvent(KeyInputEvent event) {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().currentScreen == null) {
				Input input = InputMappings.Type.KEYSYM.getOrMakeInput(event.getKey());
				for (KeyBinding keybinding : controllEngine.keyHash.lookupAll(input)) {
					if(controllEngine.keyFunctionMap.containsKey(keybinding)) {
						controllEngine.keyFunctionMap.get(keybinding).accept(event.getKey(), event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void moveInputEvent(InputUpdateEvent event) {
			if(controllEngine.playerdata == null) {
				return;
			}
			
			Minecraft minecraft = Minecraft.getInstance();
			EntityState playerState = controllEngine.playerdata.getEntityState();
			
			if(!controllEngine.playerCanRotate(playerState) && controllEngine.player.isAlive()) {
				GLFW.glfwSetCursorPosCallback(minecraft.getMainWindow().getHandle(), controllEngine.callback);
				minecraft.mouseHelper.mouseX = controllEngine.tracingMouseX;
				minecraft.mouseHelper.mouseY = controllEngine.tracingMouseY;
			} else {
				controllEngine.tracingMouseX = minecraft.mouseHelper.getMouseX();
				controllEngine.tracingMouseY = minecraft.mouseHelper.getMouseY();
				minecraft.mouseHelper.registerCallbacks(Minecraft.getInstance().getMainWindow().getHandle());
			}
			
			if (!controllEngine.playerCanMove(playerState)) {
				event.getMovementInput().moveForward = 0F;
				event.getMovementInput().moveStrafe = 0F;
				event.getMovementInput().forwardKeyDown = false;
				event.getMovementInput().backKeyDown = false;
				event.getMovementInput().leftKeyDown = false;
				event.getMovementInput().rightKeyDown = false;
				event.getMovementInput().jump = false;
				event.getMovementInput().sneaking = false;
				((ClientPlayerEntity)event.getPlayer()).sprintToggleTimer = -1;
			}
		}
		
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.START) {
				if (Minecraft.getInstance().player != null) {
					controllEngine.tick();
				}
			}
		}
	}
}