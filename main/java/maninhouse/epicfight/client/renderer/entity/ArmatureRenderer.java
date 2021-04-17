package maninhouse.epicfight.client.renderer.entity;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.animation.types.attack.AttackAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.client.model.ClientModel;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.ModRenderTypes;
import maninhouse.epicfight.client.renderer.layer.Layer;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public abstract class ArmatureRenderer<E extends LivingEntity, T extends LivingData<E>> {
	protected List<Layer<E, T>> layers;
	
	public ArmatureRenderer() {
		this.layers = Lists.newArrayList();
	}
	
	public void render(E entityIn, T entitydata, EntityRenderer<E> renderer, IRenderTypeBuffer buffer, MatrixStack matStack, int packedLightIn, float partialTicks) {
		if(this.shouldRenderNameTag(entitydata, entityIn)) {
			RenderNameplateEvent renderNameplateEvent = new RenderNameplateEvent(entityIn, entityIn.getDisplayName(), renderer, matStack, buffer, packedLightIn, partialTicks);
		    MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
			this.renderNameTag(entitydata, entityIn, renderNameplateEvent.getContent(), matStack, buffer, packedLightIn);
		}
		
		Minecraft mc = Minecraft.getInstance();
		boolean flag = this.isVisible(entityIn);
		boolean flag1 = !flag && !entityIn.isInvisibleToPlayer(mc.player);
		boolean flag2 = mc.isEntityGlowing(entityIn);
		RenderType renderType = this.getRenderType(entityIn, entitydata, flag, flag1, flag2);
		
		if(renderType != null) {
			IVertexBuilder builder = buffer.getBuffer(renderType);
			ClientModel model = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
			Armature armature = model.getArmature();
			armature.initializeTransform();
			matStack.push();
			this.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
			entitydata.getClientAnimator().setPoseToModel(partialTicks);
			VisibleMatrix4f[] poses = armature.getJointTransforms();
			model.draw(matStack, builder, packedLightIn, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F, poses);
			
			if(!entityIn.isSpectator()) {
				renderLayer(entitydata, entityIn, poses, buffer, matStack, packedLightIn, partialTicks);
			}
			
			if(Minecraft.getInstance().getRenderManager().isDebugBoundingBox()) {
				AnimatorClient animator = entitydata.getClientAnimator();
				AnimationPlayer player = animator.getPlayer();
				if(player.getPlay() instanceof AttackAnimation) {
					AttackAnimation attackAnimation = (AttackAnimation) player.getPlay();
					boolean flag3 = entitydata.getEntityState().shouldDetectCollision();
					float elapsedTime = player.getElapsedTime();
					int index = attackAnimation.getIndexer(elapsedTime);
					Collider collider = attackAnimation.getCollider((LivingData<?>) entitydata, elapsedTime);
					VisibleMatrix4f mat = null;
					
					if (index > 0) {
						Joint joint = armature.getJointHierarcy();
						while(index >> 5 != 0) {
							index = index >> 5;
							joint = joint.getSubJoints().get((index & 31) - 1);
						}
						mat = joint.getAnimatedTransform();
					}
					
					if (mat == null) {
						mat = new VisibleMatrix4f();
					}
					collider.draw(matStack, buffer, mat, partialTicks, flag3);
				}
			}
			
			matStack.pop();
		}
	}
	
	public RenderType getRenderType(E entityIn, T entitydata, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
		ResourceLocation resourcelocation = this.getEntityTexture(entityIn);
		if (isVisibleToPlayer) {
			return ModRenderTypes.getItemEntityTranslucentCull(resourcelocation);
		} else if (isVisible) {
			return this.getCommonRenderType(resourcelocation);
		} else {
			return isGlowing ? RenderType.getOutline(resourcelocation) : null;
		}
	}
	
	protected abstract ResourceLocation getEntityTexture(E entityIn);
	
	protected void renderLayer(T entitydata, E entityIn, VisibleMatrix4f[] poses, IRenderTypeBuffer buffer, MatrixStack matrixStackIn, int packedLightIn, float partialTicks) {
		for(Layer<E, T> layer : this.layers) {
			layer.renderLayer(entitydata, entityIn, matrixStackIn, buffer, packedLightIn, poses, partialTicks);
		}
	}
	
	protected boolean isVisible(E entityIn) {
		return !entityIn.isInvisible();
	}

	protected RenderType getCommonRenderType(ResourceLocation resourcelocation) {
		return ModRenderTypes.getAnimatedModel(resourcelocation);
	}
	
	protected void transformJoint(int jointId, Armature modelArmature, VisibleMatrix4f mat) {
		Joint joint = modelArmature.findJointById(jointId);
        VisibleMatrix4f.mul(joint.getAnimatedTransform(), mat, joint.getAnimatedTransform());
	}
	
	protected void applyRotations(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
        VisibleMatrix4f transpose = entitydata.getModelMatrix(partialTicks).transpose();
        matStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        MathUtils.rotateStack(matStack, transpose);
        MathUtils.scaleStack(matStack, transpose);
	}
	
	protected boolean shouldRenderNameTag(T entitydata, E entity) {
		boolean flag1;
		double d0 = Minecraft.getInstance().renderViewEntity.getDistanceSq(entity);
		float f = entity.isDiscrete() ? 32.0F : 64.0F;
		if (d0 >= (double) (f * f)) {
			flag1 = false;
		} else {
			Minecraft minecraft = Minecraft.getInstance();
			ClientPlayerEntity clientplayerentity = minecraft.player;
			boolean flag = !entity.isInvisibleToPlayer(clientplayerentity);
			if (entity != clientplayerentity) {
				Team team = entity.getTeam();
				Team team1 = clientplayerentity.getTeam();
				if (team != null) {
					Team.Visible team$visible = team.getNameTagVisibility();
					switch (team$visible) {
					case ALWAYS:
						flag1 = flag;
					case NEVER:
						flag1 = false;
					case HIDE_FOR_OTHER_TEAMS:
						flag1 = (team1 == null) ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
					case HIDE_FOR_OWN_TEAM:
						flag1 = (team1 == null) ? flag : !team.isSameTeam(team1) && flag;
					default:
						flag1 = true;
					}
				}
			}
			flag1 = Minecraft.isGuiEnabled() && entity != minecraft.getRenderViewEntity() && flag
					&& !entity.isBeingRidden();
		}

		return flag1 && (entity.getAlwaysRenderNameTagForRender()
				|| entity.hasCustomName() && (entity == Minecraft.getInstance().getRenderManager().pointedEntity
						|| entity instanceof PlayerEntity));
	}
	
	protected void renderNameTag(T entitydata, E entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		
		double d0 = renderManager.squareDistanceTo(entityIn);
		if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(entityIn, d0)) {
			boolean flag = !entityIn.isDiscrete();
			float f = entityIn.getHeight() + 0.5F;
			int i = "deadmau5".equals(displayNameIn.getString()) ? -10 : 0;
			matrixStackIn.push();
			matrixStackIn.translate(0.0D, (double) f, 0.0D);
			matrixStackIn.rotate(renderManager.getCameraOrientation());
			matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
			float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			FontRenderer fontrenderer = renderManager.getFontRenderer();
			float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
			fontrenderer.func_243247_a(displayNameIn, f2, (float) i, 553648127, false, matrix4f, bufferIn, flag, j,
					packedLightIn);
			if (flag) {
				fontrenderer.func_243247_a(displayNameIn, f2, (float) i, -1, false, matrix4f, bufferIn, false, 0,
						packedLightIn);
			}

			matrixStackIn.pop();
		}
	}
}