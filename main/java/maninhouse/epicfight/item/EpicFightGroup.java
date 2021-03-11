package maninhouse.epicfight.item;

import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class EpicFightGroup {
	public static final ItemGroup ITEMS = new ItemGroup(EpicFightMod.MODID + ".skillbook") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SKILLBOOK.get());
        }
    };
}