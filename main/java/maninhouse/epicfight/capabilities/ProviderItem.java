package maninhouse.epicfight.capabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import maninhouse.epicfight.capabilities.item.ArmorCapability;
import maninhouse.epicfight.capabilities.item.AxeCapability;
import maninhouse.epicfight.capabilities.item.BowCapability;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.capabilities.item.CrossbowCapability;
import maninhouse.epicfight.capabilities.item.HoeCapability;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.capabilities.item.PickaxeCapability;
import maninhouse.epicfight.capabilities.item.ShovelCapability;
import maninhouse.epicfight.capabilities.item.SwordCapability;
import maninhouse.epicfight.capabilities.item.TridentCapability;
import maninhouse.epicfight.capabilities.item.VanillaArmorCapability;
import maninhouse.epicfight.config.CapabilityConfig;
import maninhouse.epicfight.config.CapabilityConfig.CustomArmorConfig;
import maninhouse.epicfight.config.CapabilityConfig.CustomWeaponConfig;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistries;

public class ProviderItem implements ICapabilityProvider, NonNullSupplier<CapabilityItem> {
	private static final Map<Item, CapabilityItem> CAPABILITY_BY_INSTANCE = new HashMap<Item, CapabilityItem> ();
	private static final Map<Class<? extends Item>, Function<Item, CapabilityItem>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends Item>, Function<Item, CapabilityItem>> ();
	
	public static void makeMap() {
		CAPABILITY_BY_INSTANCE.put(Items.WOODEN_AXE, new AxeCapability(Items.WOODEN_AXE));
		CAPABILITY_BY_INSTANCE.put(Items.STONE_AXE, new AxeCapability(Items.STONE_AXE));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_AXE, new AxeCapability(Items.IRON_AXE));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_AXE, new AxeCapability(Items.GOLDEN_AXE));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_AXE, new AxeCapability(Items.DIAMOND_AXE));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_AXE, new AxeCapability(Items.NETHERITE_AXE));
		CAPABILITY_BY_INSTANCE.put(Items.WOODEN_PICKAXE, new PickaxeCapability(Items.WOODEN_PICKAXE));
		CAPABILITY_BY_INSTANCE.put(Items.STONE_PICKAXE, new PickaxeCapability(Items.STONE_PICKAXE));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_PICKAXE, new PickaxeCapability(Items.IRON_PICKAXE));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_PICKAXE, new PickaxeCapability(Items.GOLDEN_PICKAXE));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_PICKAXE, new PickaxeCapability(Items.DIAMOND_PICKAXE));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_PICKAXE, new PickaxeCapability(Items.NETHERITE_PICKAXE));
		CAPABILITY_BY_INSTANCE.put(Items.WOODEN_HOE, new HoeCapability(Items.WOODEN_HOE));
		CAPABILITY_BY_INSTANCE.put(Items.STONE_HOE, new HoeCapability(Items.STONE_HOE));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_HOE, new HoeCapability(Items.IRON_HOE));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_HOE, new HoeCapability(Items.GOLDEN_HOE));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_HOE, new HoeCapability(Items.DIAMOND_HOE));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_HOE, new HoeCapability(Items.NETHERITE_HOE));
		CAPABILITY_BY_INSTANCE.put(Items.WOODEN_SHOVEL, new ShovelCapability(Items.WOODEN_SHOVEL));
		CAPABILITY_BY_INSTANCE.put(Items.STONE_SHOVEL, new ShovelCapability(Items.STONE_SHOVEL));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_SHOVEL, new ShovelCapability(Items.IRON_SHOVEL));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_SHOVEL, new ShovelCapability(Items.GOLDEN_SHOVEL));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_SHOVEL, new ShovelCapability(Items.DIAMOND_SHOVEL));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_SHOVEL, new ShovelCapability(Items.NETHERITE_SHOVEL));
		CAPABILITY_BY_INSTANCE.put(Items.WOODEN_SWORD, new SwordCapability(Items.WOODEN_SWORD));
		CAPABILITY_BY_INSTANCE.put(Items.STONE_SWORD, new SwordCapability(Items.STONE_SWORD));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_SWORD, new SwordCapability(Items.IRON_SWORD));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_SWORD, new SwordCapability(Items.GOLDEN_SWORD));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_SWORD, new SwordCapability(Items.DIAMOND_SWORD));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_SWORD, new SwordCapability(Items.NETHERITE_SWORD));
		CAPABILITY_BY_INSTANCE.put(Items.LEATHER_BOOTS, new VanillaArmorCapability((ArmorItem)Items.LEATHER_BOOTS));
		CAPABILITY_BY_INSTANCE.put(Items.LEATHER_CHESTPLATE, new VanillaArmorCapability((ArmorItem)Items.LEATHER_CHESTPLATE));
		CAPABILITY_BY_INSTANCE.put(Items.LEATHER_HELMET, new VanillaArmorCapability((ArmorItem)Items.LEATHER_HELMET));
		CAPABILITY_BY_INSTANCE.put(Items.LEATHER_LEGGINGS, new VanillaArmorCapability((ArmorItem)Items.LEATHER_LEGGINGS));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_BOOTS, new VanillaArmorCapability((ArmorItem)Items.GOLDEN_BOOTS));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_CHESTPLATE, new VanillaArmorCapability((ArmorItem)Items.GOLDEN_CHESTPLATE));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_HELMET, new VanillaArmorCapability((ArmorItem)Items.GOLDEN_HELMET));
		CAPABILITY_BY_INSTANCE.put(Items.GOLDEN_LEGGINGS, new VanillaArmorCapability((ArmorItem)Items.GOLDEN_LEGGINGS));
		CAPABILITY_BY_INSTANCE.put(Items.CHAINMAIL_BOOTS, new VanillaArmorCapability((ArmorItem)Items.CHAINMAIL_BOOTS));
		CAPABILITY_BY_INSTANCE.put(Items.CHAINMAIL_CHESTPLATE, new VanillaArmorCapability((ArmorItem)Items.CHAINMAIL_CHESTPLATE));
		CAPABILITY_BY_INSTANCE.put(Items.CHAINMAIL_HELMET, new VanillaArmorCapability((ArmorItem)Items.CHAINMAIL_HELMET));
		CAPABILITY_BY_INSTANCE.put(Items.CHAINMAIL_LEGGINGS, new VanillaArmorCapability((ArmorItem)Items.CHAINMAIL_LEGGINGS));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_BOOTS, new VanillaArmorCapability((ArmorItem)Items.IRON_BOOTS));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_CHESTPLATE, new VanillaArmorCapability((ArmorItem)Items.IRON_CHESTPLATE));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_HELMET, new VanillaArmorCapability((ArmorItem)Items.IRON_HELMET));
		CAPABILITY_BY_INSTANCE.put(Items.IRON_LEGGINGS, new VanillaArmorCapability((ArmorItem)Items.IRON_LEGGINGS));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_BOOTS, new VanillaArmorCapability((ArmorItem)Items.DIAMOND_BOOTS));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_CHESTPLATE, new VanillaArmorCapability((ArmorItem)Items.DIAMOND_CHESTPLATE));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_HELMET, new VanillaArmorCapability((ArmorItem)Items.DIAMOND_HELMET));
		CAPABILITY_BY_INSTANCE.put(Items.DIAMOND_LEGGINGS, new VanillaArmorCapability((ArmorItem)Items.DIAMOND_LEGGINGS));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_BOOTS, new VanillaArmorCapability((ArmorItem)Items.NETHERITE_BOOTS));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_CHESTPLATE, new VanillaArmorCapability((ArmorItem)Items.NETHERITE_CHESTPLATE));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_HELMET, new VanillaArmorCapability((ArmorItem)Items.NETHERITE_HELMET));
		CAPABILITY_BY_INSTANCE.put(Items.NETHERITE_LEGGINGS, new VanillaArmorCapability((ArmorItem)Items.NETHERITE_LEGGINGS));
		CAPABILITY_BY_INSTANCE.put(Items.BOW, new BowCapability(Items.BOW));
		CAPABILITY_BY_INSTANCE.put(Items.CROSSBOW, new CrossbowCapability(Items.CROSSBOW));
		CAPABILITY_BY_INSTANCE.put(Items.TRIDENT, new TridentCapability(Items.TRIDENT));
		
		addCustomItemCapabilities();
		
		CAPABILITY_BY_CLASS.put(ArmorItem.class, ArmorCapability::new);
		CAPABILITY_BY_CLASS.put(SwordItem.class, SwordCapability::new);
		CAPABILITY_BY_CLASS.put(PickaxeItem.class, PickaxeCapability::new);
		CAPABILITY_BY_CLASS.put(AxeItem.class, AxeCapability::new);
		CAPABILITY_BY_CLASS.put(ShovelItem.class, ShovelCapability::new);
		CAPABILITY_BY_CLASS.put(HoeItem.class, HoeCapability::new);
		CAPABILITY_BY_CLASS.put(BowItem.class, BowCapability::new);
		CAPABILITY_BY_CLASS.put(CrossbowItem.class, CrossbowCapability::new);
	}
	
	public static void addCustomItemCapabilities() {
		for (CustomWeaponConfig config : CapabilityConfig.CUSTOM_WEAPON_LISTS) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(config.getRegistryName()));
			if (item != null) {
				EpicFightMod.LOGGER.info("Register Custom Capaiblity for " + config.getRegistryName());
				ModWeaponCapability cap = config.getWeaponType().get();
				cap.setOneHandStyleAttribute(config.getArmorIgnoranceOnehand(), config.getImpactOnehand(), config.getHitAtOnceOnehand());
				cap.setTwoHandStyleAttribute(config.getArmorIgnoranceTwohand(), config.getImpactTwohand(), config.getHitAtOnceTwohand());
				CAPABILITY_BY_INSTANCE.put(item, cap);
			} else {
				EpicFightMod.LOGGER.warn("Failed to load custom item " + config.getRegistryName() + ". Item not exist!");
			}
		}
		
		for (CustomArmorConfig config : CapabilityConfig.CUSTOM_ARMOR_LISTS) {
			try {
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(config.getRegistryName()));
				if (item != null && !(item instanceof ArmorItem)) {
					ArmorCapability cap = new ArmorCapability(item, config.getWeight(), config.getStunArmor());
					CAPABILITY_BY_INSTANCE.put(item, cap);
					EpicFightMod.LOGGER.info("Register Custom Capaiblity for " + config.getRegistryName());
				} else {
					EpicFightMod.LOGGER.warn("Failed to load custom item " + config.getRegistryName() + ". Item not exist!");
				}
			} catch (Exception e) {
				EpicFightMod.LOGGER.warn("Failed to load custom item " + config.getRegistryName());
				System.err.println(e);
			}
		}
	}
	
	private CapabilityItem capability;
	private LazyOptional<CapabilityItem> optional = LazyOptional.of(this);
	
	public ProviderItem(Item item) {
		capability = CAPABILITY_BY_INSTANCE.get(item);
		
		if (capability == null) {
			capability = this.makeCustomCapability(item);
			if(capability != null) {
				CAPABILITY_BY_INSTANCE.put(item, capability);
			}
		}
	}
	
	public boolean hasCapability() {
		return this.capability != null;
	}
	
	private CapabilityItem makeCustomCapability(Item item) {
		Class<?> clazz = item.getClass();
		CapabilityItem cap = null;
		for (; clazz != null && cap == null; clazz = clazz.getSuperclass())
			cap = CAPABILITY_BY_CLASS.getOrDefault(clazz, (argIn) -> null).apply(item);
		return cap;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_ITEM ? optional.cast() : LazyOptional.empty();
	}

	@Override
	public CapabilityItem get() {
		return capability;
	}
}