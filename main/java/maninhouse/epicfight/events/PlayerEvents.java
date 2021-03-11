package maninhouse.epicfight.events;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class PlayerEvents
{	
	@SubscribeEvent
	public static void arrowLooseEvent(ArrowLooseEvent event)
	{
		Colliders.update();
	}
	
	@SubscribeEvent
	public static void itemUseStartEvent(LivingEntityUseItemEvent.Start event)
	{
		if(event.getEntity() instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)event.getEntity();
			PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			CapabilityItem itemCap = playerdata.getHeldItemCapability(Hand.MAIN_HAND);
			
			if(playerdata.isInaction())
			{
				event.setCanceled(true);
			}
			else if(event.getItem() == player.getHeldItemOffhand() && itemCap != null && itemCap.isTwoHanded())
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event)
	{
		PlayerData<?> playerdata = (PlayerData<?>) event.getOriginal().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if(playerdata.getOriginalEntity() != null)
		{
			playerdata.discard();
		}
	}
	
	@SubscribeEvent
	public static void itemUseStopEvent(LivingEntityUseItemEvent.Stop event)
	{
		if(event.getEntity().world.isRemote)
		{
			if(event.getEntity() instanceof ClientPlayerEntity)
			{
				ClientEngine.INSTANCE.renderEngine.zoomOut(0);
			}
		}
	}
	
	@SubscribeEvent
	public static void itemUseTickEvent(LivingEntityUseItemEvent.Tick event)
	{
		if(event.getEntity() instanceof PlayerEntity)
		{
			if(event.getItem().getItem() instanceof BowItem)
			{
				PlayerData<?> playerdata = (PlayerData<?>) event.getEntity().getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if(playerdata.isInaction())
				{
					event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void itemTossEvent(ItemTossEvent event)
	{
		
	}
}