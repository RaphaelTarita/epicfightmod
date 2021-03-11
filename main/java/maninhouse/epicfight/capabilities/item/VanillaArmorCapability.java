package maninhouse.epicfight.capabilities.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class VanillaArmorCapability extends ArmorCapability {
	public VanillaArmorCapability(ArmorItem item) {
		super(item);
		if(item.getArmorMaterial() instanceof ArmorMaterial) {
			switch((ArmorMaterial) item.getArmorMaterial()) {
			case LEATHER:
				this.weight = item.getDamageReduceAmount();
				this.stunArmor = item.getDamageReduceAmount() * 0.25D;
				break;
			case GOLD:
				this.weight = item.getDamageReduceAmount() * 2.0D;
				this.stunArmor = item.getDamageReduceAmount() * 0.3D;
				break;
			case CHAIN:
				this.weight = item.getDamageReduceAmount() * 2.5D;
				this.stunArmor = item.getDamageReduceAmount() * 0.375D;
				break;
			case IRON:
				this.weight = item.getDamageReduceAmount() * 3.0D;
				this.stunArmor = item.getDamageReduceAmount() * 0.5D;
				break;
			case DIAMOND:
				this.weight = item.getDamageReduceAmount() * 3.0D;
				this.stunArmor = item.getDamageReduceAmount() * 0.5D;
				break;
			case NETHERITE:
				this.weight = item.getDamageReduceAmount() * 3.2D;
				this.stunArmor = item.getDamageReduceAmount() * 0.75D;
				break;
			default:
				this.weight = 0.0D;
				this.stunArmor = 0.0D;
			}
		} else {
			this.weight = 0.0D;
			this.stunArmor = 0.0D;
		}
	}
}