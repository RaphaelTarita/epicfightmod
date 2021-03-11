package maninhouse.epicfight.client.events;

import java.util.Map;

import maninhouse.epicfight.client.particle.BlastParticle;
import maninhouse.epicfight.client.particle.BlastPunchHugeParticle;
import maninhouse.epicfight.client.particle.BlastPunchParticle;
import maninhouse.epicfight.client.particle.BloodParticle;
import maninhouse.epicfight.client.particle.CutParticle;
import maninhouse.epicfight.client.particle.DustParticle;
import maninhouse.epicfight.client.particle.HitBluntParticle;
import maninhouse.epicfight.client.particle.HitCutParticle;
import maninhouse.epicfight.client.particle.ParryParticle;
import maninhouse.epicfight.client.particle.PortalStraightParticle;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.particle.Particles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid=EpicFightMod.MODID, value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public class RegistryClientEvent
{
	static AtlasTexture particleTexture;
	static Map<ResourceLocation, IBakedModel> modelRegistry;
	static ModelLoader modelLoader;
	
	@SubscribeEvent
    public static void onModelRegistry(final ModelBakeEvent event)
    {
    	modelRegistry = event.getModelRegistry();
    	modelLoader = event.getModelLoader();
    }
    
	@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(final ParticleFactoryRegisterEvent event)
    {
    	Minecraft.getInstance().particles.registerFactory(Particles.PORTAL_STRAIGHT.get(), PortalStraightParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.HIT_BLUNT.get(), HitBluntParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.HIT_CUT.get(), new HitCutParticle.Factory());
    	Minecraft.getInstance().particles.registerFactory(Particles.BLOOD.get(), BloodParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.FLASH.get(), BlastParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.CUT.get(), CutParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.PARRY.get(), ParryParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.BLAST_PUNCH.get(), new BlastPunchParticle.Factory());
    	Minecraft.getInstance().particles.registerFactory(Particles.BLAST_PUNCH_HUGE.get(), new BlastPunchHugeParticle.Factory());
    	Minecraft.getInstance().particles.registerFactory(Particles.DUST.get(), DustParticle.Factory::new);
    }
	
    @SubscribeEvent
    public static void onTextureRegistry(final TextureStitchEvent.Pre event)
    {
    	if(event.getMap().getTextureLocation().getPath() == "textures/particle")
    	{
    		particleTexture = event.getMap();
    		event.addSprite(location("blast_punch_huge"));
    	}
    }
    
    @SubscribeEvent
    public static void onTextureRegistryPost(final TextureStitchEvent.Post event)
    {
    	if(event.getMap().getTextureLocation().getPath() == "textures/particle")
    	{
    		//registerParticleOBJModel(modelRegistry, modelLoader, Particles.BLAST_PUNCH_HUGE.get());
    	}
    }
    /**
    private static void registerItemOBJModel(ModelBakeEvent event, Item item, ImmutableMap<TransformType, TransformationMatrix> transforms)
    {
    	registerItemOBJModel(event, item, item.toString(), null, transforms);
    }
    
    private static void registerItemOBJModel(ModelBakeEvent event, Item item, String objFileName, String replaceTextureName, ImmutableMap<TransformType, TransformationMatrix> transforms)
    {
    	IUnbakedModel model = ModelLoaderRegistry.getModelOrLogError(location("item/" + objFileName + ".obj"), "Missing model");
    	
        if (model instanceof OBJModel)
        {
        	if(replaceTextureName != null)
        	{
        		IUnbakedModel retextured = null;
        		for(String s : ((OBJModel) model).getMatLib().getMaterialNames())
            	{
            		Material mat = ((OBJModel) model).getMatLib().getMaterial(s);
            		String path = mat.getTexture().getPath();
            		
            		if(path.contains(EpicFightMod.MODID))
            		{
            			retextured = model.retexture(ImmutableMap.of("#" + path, location("item/" + replaceTextureName).toString()));
            		}
            	}
        		
        		if(retextured != null)
        			model = retextured;
        	}
        	
        	model = model.process(ImmutableMap.<String, String>builder().put("flip-v", "true").build());
            IBakedModel bakedInvModel = model.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), true), DefaultVertexFormats.ITEM);
            bakedInvModel = new PerspectiveMapWrapper(bakedInvModel, transforms);
            event.getModelRegistry().put(new ModelResourceLocation(EpicFightMod.MODID + ":" + item.toString(), "inventory"), bakedInvModel);
        }
    }
    
    private static void registerParticleOBJModel(Map<ResourceLocation, IBakedModel> modelregistry, ModelLoader modelloader, BasicParticleType particleType)
    {
    	String name = particleType.getRegistryName().getPath();
    	IUnbakedModel model = ModelLoaderRegistry.getModelOrLogError(location("particle/" + name + ".obj"), "Missing model");
    	
    	if (model instanceof OBJModel)
        {
    		model = model.process(ImmutableMap.<String, String>builder().put("flip-v", "true").build());
    		IBakedModel bakedInvModel = model.bake(modelloader, (resource)->
    		{
    			ResourceLocation rl;
    			if(resource.getNamespace().equals(EpicFightMod.MODID))
    				rl = new ResourceLocation(resource.getNamespace(), resource.getPath().substring(9));
    			else
    				rl = resource;
    			return particleTexture.getSprite(rl);
    		}, new BasicState(model.getDefaultState(), true), DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
    		modelregistry.put(new ModelResourceLocation(EpicFightMod.MODID + ":" + name, "particle"), bakedInvModel);
        }
    }**/
    private static ResourceLocation location(String path)
    {
    	return new ResourceLocation(EpicFightMod.MODID, path);
    }
}