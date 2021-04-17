package maninhouse.epicfight.client.gui;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.skill.Skill;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.skill.SkillSlot;
import maninhouse.epicfight.utils.math.Vec2f;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BattleModeGui extends ModIngameGui {
	private final Map<SkillSlot, Vec3f> screenPositionMap;
	private int guiSlider;
	private boolean guiSliderToggle;
	protected FontRenderer font;
	
	public BattleModeGui() {
		guiSlider = 29;
		guiSliderToggle = false;
		screenPositionMap = new HashMap<SkillSlot, Vec3f> ();
		screenPositionMap.put(SkillSlot.DODGE, new Vec3f(74F, 36F, 0.078F));
		screenPositionMap.put(SkillSlot.WEAPON_SPECIAL_ATTACK, new Vec3f(42F, 48F, 0.117F));
		font = Minecraft.getInstance().fontRenderer;
	}
	
	private static final Vec2f[] vectorz = {
		new Vec2f(0.5F, 0.5F),
		new Vec2f(0.5F, 0.0F),
		new Vec2f(0.0F, 0.0F),
		new Vec2f(0.0F, 1.0F),
		new Vec2f(1.0F, 1.0F),
		new Vec2f(1.0F, 0.0F)
	};
	
	public void renderGui(ClientPlayerData playerdata, float partialTicks) {
		if (playerdata.getOriginalEntity().getRidingEntity() != null) {
			return;
		}

		if (guiSlider > 28) {
			return;
		} else if (guiSlider > 0) {
			if (this.guiSliderToggle) {
				guiSlider -= 2;
			} else {
				guiSlider += 2;
			}
		}
		
		MainWindow sr = Minecraft.getInstance().getMainWindow();
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();
		
		boolean depthTestEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		boolean alphaTestEnabled = GL11.glGetBoolean(GL11.GL_ALPHA_TEST);
		boolean blendEnabled = GL11.glGetBoolean(GL11.GL_BLEND);
		
		if(!depthTestEnabled)
			GlStateManager.enableDepthTest();
		if(!alphaTestEnabled)
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		if(!blendEnabled)
			GlStateManager.enableBlend();
		
		MatrixStack matStack = new MatrixStack();
		
		Minecraft.getInstance().getTextureManager().bindTexture(EntityIndicator.BATTLE_ICON);
		
		float maxStunArmor = playerdata.getMaxStunArmor();
		float stunArmor = playerdata.getStunArmor();
		
		if(maxStunArmor > 0.0F && stunArmor < maxStunArmor) {
			float ratio = stunArmor / maxStunArmor;
			matStack.push();
			matStack.translate(0, (float)guiSlider * 0.5F, 0);
			matStack.scale(0.5F, 0.5F, 1.0F);
			GlStateManager.color4f(1.0F, ratio, 0.25F, 1.0F);
			AbstractGui.blit(matStack, (int)((width-120) * 2.0F), (int)((height-10) * 2.0F), 2.0F, 38.0F, 237, 9, 255, 255);
			AbstractGui.blit(matStack, (int)((width-120) * 2.0F), (int)((height-10) * 2.0F), 2.0F, 47.0F, (int)(237*ratio), 9, 255, 255);
			matStack.pop();
		}
		
		for (int i = 0; i < SkillSlot.values().length; i++) {
			SkillContainer container = playerdata.getSkill(i);
			
			if(container != null && !container.isEmpty() && this.screenPositionMap.containsKey(container.getContaining().getSlot())) {
				SkillSlot slot = container.getContaining().getSlot();
				boolean creative = playerdata.getOriginalEntity().isCreative();
				float cooldownRatio = creative ? 1.0F : container.getCooldownRatio(partialTicks);
				float durationRatio = container.getDurationRatio(partialTicks);
				boolean isReady = container.getStack() > 0 || durationRatio > 0 || creative;
				boolean fullstack = container.getStack() >= container.getContaining().getMaxStack();
				int x = (int) this.screenPositionMap.get(slot).x;
				int y = (int) this.screenPositionMap.get(slot).y;
				float scale = this.screenPositionMap.get(slot).z;
				float multiplyScale = 1F / scale;
				
				matStack.push();
				matStack.scale(scale, scale, 1.0F);
				matStack.translate(0, (float)guiSlider * multiplyScale, 0);
				Matrix4f matrix = matStack.getLast().getMatrix();
				
				if(!isReady) {
					GlStateManager.color4f(0.5F, 0.5F, 0.5F, 0.8F);
				} else {
					GlStateManager.color4f(1F, 1F, 1F, 1F);
				}
				
				if (slot != SkillSlot.WEAPON_SPECIAL_ATTACK) {
					Minecraft.getInstance().getTextureManager().bindTexture(this.getSkillTexture(container.getContaining()));
					drawTexturedModalRectFixCoord(matrix, (width - x) * multiplyScale, (height - y) * multiplyScale, 0, 0, 255, 255);
					
					if (!(fullstack || creative)) {
						matStack.scale(multiplyScale, multiplyScale, 1.0F);
						this.font.drawStringWithShadow(matStack,
								String.valueOf((int)(1 + container.getCooldownSec() * Math.max(1 / container.getContaining().getRegenTimePerTick(playerdata), 1.0F))),
								((float)width - x+8), ((float)height - y+8), 16777215);
						GL11.glEnable(GL11.GL_ALPHA_TEST);
						GlStateManager.enableBlend();
					}
				} else {
					CapabilityItem item = playerdata.getHeldItemCapability(Hand.MAIN_HAND);
					boolean isCompatibleWeapon = item != null && item.getSpecialAttack(playerdata) == container.getContaining();
					int vertexNum = 0;
					float iconSize = 32.0F;
					float iconSizeDiv = iconSize*0.5F;
					float top = y;
					float bottom = y - iconSize;
					float left = x;
					float right = x - iconSize;
					float middle = x - iconSizeDiv;
					float lastVertexX = 0;
					float lastVertexY = 0;
					float lastTexX = 0;
					float lastTexY = 0;
					
					if (cooldownRatio < 0.125F) {
						vertexNum = 6;
						lastTexX = cooldownRatio / 0.25F;
						lastTexY = 0.0F;
						lastVertexX = middle - iconSize * lastTexX;
						lastVertexY = top;
						lastTexX+=0.5F;
					} else if (cooldownRatio < 0.375F) {
						vertexNum = 5;
						lastTexX = 1.0F;
						lastTexY = (cooldownRatio-0.125F) / 0.25F;
						lastVertexX = right;
						lastVertexY = top - iconSize * lastTexY;
					} else if (cooldownRatio < 0.625F) {
						vertexNum = 4;
						lastTexX = (cooldownRatio-0.375F) / 0.25F;
						lastTexY = 1.0F;
						lastVertexX = right + iconSize * lastTexX;
						lastVertexY = bottom;
						lastTexX = 1.0F - lastTexX;
					} else if (cooldownRatio < 0.875F) {
						vertexNum = 3;
						lastTexX = 0.0F;
						lastTexY = (cooldownRatio-0.625F) / 0.25F;
						lastVertexX = left;
						lastVertexY = bottom + iconSize * lastTexY;
						lastTexY = 1.0F - lastTexY;
					} else {
						vertexNum = 2;
						lastTexX = (cooldownRatio-0.875F) / 0.25F;
						lastTexY = 0.0F;
						lastVertexX = left - iconSize * lastTexX;
						lastVertexY = top;
					}
					
					Minecraft.getInstance().getTextureManager().bindTexture(this.getSkillTexture(container.getContaining()));
					
					if(isCompatibleWeapon) {
						GlStateManager.color4f(0.0F, 0.5F, 0.5F, 0.6F);
					} else {
						GlStateManager.color4f(0.5F, 0.5F, 0.5F, 0.6F);
					}
					
					Tessellator tessellator = Tessellator.getInstance();
			        BufferBuilder bufferbuilder = tessellator.getBuffer();
			        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
			        
					for (int j = 0; j < vertexNum; j++) {
			        	bufferbuilder.pos(matrix, (width - (left-iconSize*vectorz[j].x)) * multiplyScale, (height - (top-iconSize*vectorz[j].y)) * multiplyScale, 0.0F)
			        		.tex(vectorz[j].x, vectorz[j].y).endVertex();
					}
			        bufferbuilder.pos(matrix, (width - lastVertexX) * multiplyScale, (height - lastVertexY) * multiplyScale, 0.0F)
	        			.tex(lastTexX, lastTexY).endVertex();
			        
			        tessellator.draw();
			        
			        if(isCompatibleWeapon) {
			        	GlStateManager.color4f(0.08F, 0.89F, 0.95F, 1.0F);
					} else {
						GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					}
			        
			        GlStateManager.disableCull();
			        
			        bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);

					for (int j = 0; j < 2; j++) {
			        	bufferbuilder.pos(matrix, (width - (left-iconSize*vectorz[j].x)) * multiplyScale, (height - (top-iconSize*vectorz[j].y)) * multiplyScale, 0.0F)
			        		.tex(vectorz[j].x, vectorz[j].y).endVertex();
					}
					
					for (int j = vectorz.length - 1; j >= vertexNum; j--) {
			        	bufferbuilder.pos(matrix, (width - (left-iconSize*vectorz[j].x)) * multiplyScale, (height - (top-iconSize*vectorz[j].y)) * multiplyScale, 0.0F)
			        		.tex(vectorz[j].x, vectorz[j].y).endVertex();
					}
			        
			        bufferbuilder.pos(matrix, (width - lastVertexX) * multiplyScale, (height - lastVertexY) * multiplyScale, 0.0F)
        			.tex(lastTexX, lastTexY).endVertex();
			        
			        bufferbuilder.finishDrawing();
			        WorldVertexBufferUploader.draw(bufferbuilder);
			        
					if (!isReady) {
			        	matStack.scale(multiplyScale, multiplyScale, 1.0F);
						String s = String.valueOf((int)(cooldownRatio * 100.0F));
						int stringWidth = (this.font.getStringWidth(s) - 6) / 3;
						this.font.drawStringWithShadow(matStack, s, ((float)width - x+13-stringWidth), ((float)height - y+13), 16777215);
						GL11.glEnable(GL11.GL_ALPHA_TEST);
						GlStateManager.enableBlend();
					}
				}
				matStack.pop();
			}
		}
		
		if(!depthTestEnabled)
			GlStateManager.disableDepthTest();
		if(!alphaTestEnabled)
			GL11.glDisable(GL11.GL_ALPHA_TEST);
		if(!blendEnabled)
			GlStateManager.disableBlend();
	}
	
	private ResourceLocation getSkillTexture(Skill skill) {
		ResourceLocation name = skill.getRegistryName();
		return new ResourceLocation(name.getNamespace(), "textures/gui/skills/" + name.getPath() + ".png");
	}
	
	public void slideUp() {
		this.guiSlider = 28;
		this.guiSliderToggle = true;
	}

	public void slideDown() {
		this.guiSlider = 1;
		this.guiSliderToggle = false;
	}
}