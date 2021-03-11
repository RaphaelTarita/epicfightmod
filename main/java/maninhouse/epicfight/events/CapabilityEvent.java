package maninhouse.epicfight.events;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.ProviderEntity;
import maninhouse.epicfight.capabilities.ProviderItem;
import maninhouse.epicfight.capabilities.entity.CapabilityEntity;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class CapabilityEvent {
	@SubscribeEvent
	public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null) == null) {
			ProviderItem prov = new ProviderItem(event.getObject().getItem());
			if (prov.hasCapability()) {
				event.addCapability(new ResourceLocation(EpicFightMod.MODID, "item_cap"), prov);
			}
		}
	}
	
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		if(event.getObject().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null) == null) {
			ProviderEntity prov = new ProviderEntity(event.getObject());
			if(prov.hasCapability()) {
				CapabilityEntity entityCap = prov.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				entityCap.onEntityConstructed(event.getObject());
				event.addCapability(new ResourceLocation(EpicFightMod.MODID, "entity_cap"), prov);
			}
		}
	}
}