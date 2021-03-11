package maninhouse.epicfight.client;

import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.client.events.engine.ControllEngine;
import maninhouse.epicfight.client.events.engine.RenderEngine;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientEngine {
	public static ClientEngine INSTANCE;
	public Minecraft minecraft;
	public RenderEngine renderEngine;
	public ControllEngine inputController;
	
	private ClientPlayerData playerdata;
	private PlayerActingMode playerActingMode = PlayerActingMode.MINING;
	
	public ClientEngine() {
		INSTANCE = this;
		minecraft = Minecraft.getInstance();
		renderEngine = new RenderEngine();
		inputController = new ControllEngine();
	}
	
	public void toggleActingMode() {
		if(this.playerActingMode == PlayerActingMode.MINING) {
			this.switchToBattleMode();
		} else {
			this.switchToMiningMode();
		}
	}
	
	private void switchToMiningMode() {
		this.playerActingMode = PlayerActingMode.MINING;
		this.renderEngine.guiSkillBar.slideDown();
	}
	
	private void switchToBattleMode() {
		this.playerActingMode = PlayerActingMode.BATTLE;
		this.renderEngine.guiSkillBar.slideUp();
	}
	
	public PlayerActingMode getPlayerActingMode() {
		return this.playerActingMode;
	}
	
	public boolean isBattleMode() {
		return this.playerActingMode == PlayerActingMode.BATTLE;
	}
	
	public void setPlayerData(ClientPlayerData playerdata) {
		if(this.playerdata != null && this.playerdata != playerdata) {
			this.playerdata.discard();
		}
		this.playerdata = playerdata;
	}
	
	public ClientPlayerData getPlayerData() {
		return this.playerdata;
	}
	
	public static enum PlayerActingMode {
		MINING, BATTLE
	}
}