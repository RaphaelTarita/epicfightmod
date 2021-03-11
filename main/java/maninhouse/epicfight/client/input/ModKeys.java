package maninhouse.epicfight.client.input;

import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@OnlyIn(Dist.CLIENT)
public class ModKeys
{
	public static final KeyBinding SPECIAL_ATTACK_TOOLTIP = new KeyBinding("key." + EpicFightMod.MODID + ".show_tooltip", 80, "key." + EpicFightMod.MODID + ".gui");
	public static final KeyBinding SWITCH_MODE = new KeyBinding("key." + EpicFightMod.MODID + ".switch_mode", 82, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyBinding DODGE = new KeyBinding("key." + EpicFightMod.MODID + ".dodge", 340, "key." + EpicFightMod.MODID + ".combat");
	
	public static void registerKeys()
	{
		ClientRegistry.registerKeyBinding(SPECIAL_ATTACK_TOOLTIP);
		ClientRegistry.registerKeyBinding(SWITCH_MODE);
		ClientRegistry.registerKeyBinding(DODGE);
	}
}