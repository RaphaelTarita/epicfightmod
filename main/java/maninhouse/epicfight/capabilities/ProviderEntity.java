package maninhouse.epicfight.capabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import maninhouse.epicfight.capabilities.entity.CapabilityEntity;
import maninhouse.epicfight.capabilities.entity.mob.CaveSpiderData;
import maninhouse.epicfight.capabilities.entity.mob.CreeperData;
import maninhouse.epicfight.capabilities.entity.mob.DrownedData;
import maninhouse.epicfight.capabilities.entity.mob.EndermanData;
import maninhouse.epicfight.capabilities.entity.mob.EvokerData;
import maninhouse.epicfight.capabilities.entity.mob.HoglinData;
import maninhouse.epicfight.capabilities.entity.mob.IronGolemData;
import maninhouse.epicfight.capabilities.entity.mob.PiglinBruteData;
import maninhouse.epicfight.capabilities.entity.mob.PiglinData;
import maninhouse.epicfight.capabilities.entity.mob.PillagerData;
import maninhouse.epicfight.capabilities.entity.mob.RavagerData;
import maninhouse.epicfight.capabilities.entity.mob.SkeletonData;
import maninhouse.epicfight.capabilities.entity.mob.SpiderData;
import maninhouse.epicfight.capabilities.entity.mob.StrayData;
import maninhouse.epicfight.capabilities.entity.mob.VexData;
import maninhouse.epicfight.capabilities.entity.mob.VindicatorData;
import maninhouse.epicfight.capabilities.entity.mob.WitchData;
import maninhouse.epicfight.capabilities.entity.mob.WitherSkeletonData;
import maninhouse.epicfight.capabilities.entity.mob.ZoglinData;
import maninhouse.epicfight.capabilities.entity.mob.ZombieData;
import maninhouse.epicfight.capabilities.entity.mob.ZombieVillagerData;
import maninhouse.epicfight.capabilities.entity.mob.ZombifiedPiglinData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.client.capabilites.entity.RemoteClientPlayerData;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ProviderEntity implements ICapabilityProvider, NonNullSupplier<CapabilityEntity<?>> {
	private static final Map<EntityType<?>, Function<Entity, Supplier<CapabilityEntity<?>>>> capabilityMap = new HashMap<EntityType<?>, Function<Entity, Supplier<CapabilityEntity<?>>>> ();
	
	public static void makeMap() {
		capabilityMap.put(EntityType.PLAYER, (entityIn) -> ServerPlayerData::new);
		capabilityMap.put(EntityType.ZOMBIE, (entityIn) -> ZombieData<ZombieEntity>::new);
		capabilityMap.put(EntityType.CREEPER, (entityIn) -> CreeperData::new);
		capabilityMap.put(EntityType.ENDERMAN, (entityIn) -> EndermanData::new);
		capabilityMap.put(EntityType.SKELETON, (entityIn) -> SkeletonData<SkeletonEntity>::new);
		capabilityMap.put(EntityType.WITHER_SKELETON, (entityIn) -> WitherSkeletonData::new);
		capabilityMap.put(EntityType.STRAY, (entityIn) -> StrayData::new);
		capabilityMap.put(EntityType.ZOMBIFIED_PIGLIN, (entityIn) -> ZombifiedPiglinData::new);
		capabilityMap.put(EntityType.ZOMBIE_VILLAGER, (entityIn) -> ZombieVillagerData::new);
		capabilityMap.put(EntityType.HUSK, (entityIn) -> ZombieData<HuskEntity>::new);
		capabilityMap.put(EntityType.SPIDER, (entityIn) -> SpiderData::new);
		capabilityMap.put(EntityType.CAVE_SPIDER, (entityIn) -> CaveSpiderData::new);
		capabilityMap.put(EntityType.IRON_GOLEM, (entityIn) -> IronGolemData::new);
		capabilityMap.put(EntityType.VINDICATOR, (entityIn) -> VindicatorData::new);
		capabilityMap.put(EntityType.EVOKER, (entityIn) -> EvokerData::new);
		capabilityMap.put(EntityType.WITCH, (entityIn) -> WitchData::new);
		capabilityMap.put(EntityType.DROWNED, (entityIn) -> DrownedData::new);
		capabilityMap.put(EntityType.PILLAGER, (entityIn) -> PillagerData::new);
		capabilityMap.put(EntityType.RAVAGER, (entityIn) -> RavagerData::new);
		capabilityMap.put(EntityType.VEX, (entityIn) -> VexData::new);
		capabilityMap.put(EntityType.PIGLIN, (entityIn) -> PiglinData::new);
		capabilityMap.put(EntityType.field_242287_aj, (entityIn) -> PiglinBruteData::new);
		capabilityMap.put(EntityType.HOGLIN, (entityIn) -> HoglinData::new);
		capabilityMap.put(EntityType.ZOGLIN, (entityIn) -> ZoglinData::new);
	}
	
	public static void makeMapClient() {
		capabilityMap.put(EntityType.PLAYER, (entityIn)->{
			if(entityIn instanceof ClientPlayerEntity) {
				return ClientPlayerData::new;
			} else if (entityIn instanceof RemoteClientPlayerEntity) {
				return RemoteClientPlayerData<RemoteClientPlayerEntity>::new;
			} else if (entityIn instanceof ServerPlayerEntity) {
				return ServerPlayerData::new;
			} else {
				return ()->null;
			}
		});
	}
	
	private CapabilityEntity<?> capability;
	private LazyOptional<CapabilityEntity<?>> optional = LazyOptional.of(this);
	
	public ProviderEntity(Entity entity) {
		if(capabilityMap.containsKey(entity.getType())) {
			capability = capabilityMap.get(entity.getType()).apply(entity).get();
		}
	}
	
	public boolean hasCapability() {
		return capability != null;
	}
	
	@Override
	public CapabilityEntity<?> get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_ENTITY ? optional.cast() :  LazyOptional.empty();
	}
}