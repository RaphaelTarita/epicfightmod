package maninhouse.epicfight.item;

import maninhouse.epicfight.client.model.ClientModel;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExtendedArmorItem extends ArmorItem
{
	@OnlyIn(Dist.CLIENT)
	public ClientModel equipModel;
	
	public ExtendedArmorItem(Item.Properties builder, IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn, ClientModel equipModel)
	{
		super(materialIn, equipmentSlotIn, builder);
		
		if(EpicFightMod.isPhysicalClient())
		{
			this.equipModel = equipModel;
		}
	}
}