package maninhouse.epicfight.events;

import maninhouse.epicfight.effects.ModEffects;
import maninhouse.epicfight.effects.SuperarmorEffect;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.potion.Effect;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class RegistryEvents
{	
	@SubscribeEvent
    public static void onEffectRegistry(final RegistryEvent.Register<Effect> event)
    {
		event.getRegistry().registerAll(
			ModEffects.STUN_IMMUNITY = new SuperarmorEffect()
		);
    }
	
    @SubscribeEvent
    public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event)
    {
    	event.getRegistry().registerAll(
    		Sounds.BLADE_HIT, Sounds.BLUNT_HIT, Sounds.BLUNT_HIT_HARD, Sounds.WHOOSH, Sounds.WHOOSH_BIG, Sounds.WHOOSH_SHARP
    	);
    }
}