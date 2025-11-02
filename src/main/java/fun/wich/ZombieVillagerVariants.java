package fun.wich;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.SpawnSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

public class ZombieVillagerVariants implements ModInitializer {
	public static final String MOD_ID = "wich";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	//Drowned Villager
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_AMBIENT = register("entity.drowned_villager.ambient");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_AMBIENT_WATER = register("entity.drowned_villager.ambient_water");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_CONVERTED = register("entity.drowned_villager.converted");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_CURE = register("entity.drowned_villager.cure");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_DEATH = register("entity.drowned_villager.death");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_DEATH_WATER = register("entity.drowned_villager.death_water");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_HURT = register("entity.drowned_villager.hurt");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_HURT_WATER = register("entity.drowned_villager.hurt_water");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_SHOOT = register("entity.drowned_villager.shoot");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_STEP = register("entity.drowned_villager.step");
	public static final SoundEvent ENTITY_DROWNED_VILLAGER_SWIM = register("entity.drowned_villager.swim");
	//Villager Husk
	public static final SoundEvent ENTITY_VILLAGER_HUSK_AMBIENT = register("entity.villager_husk.ambient");
	public static final SoundEvent ENTITY_VILLAGER_HUSK_CONVERTED = register("entity.villager_husk.converted");
	public static final SoundEvent ENTITY_VILLAGER_HUSK_CONVERTED_TO_ZOMBIE = register("entity.villager_husk.converted_to_zombie");
	public static final SoundEvent ENTITY_VILLAGER_HUSK_CURE = register("entity.villager_husk.cure");
	public static final SoundEvent ENTITY_VILLAGER_HUSK_DEATH = register("entity.villager_husk.death");
	public static final SoundEvent ENTITY_VILLAGER_HUSK_HURT = register("entity.villager_husk.hurt");
	public static final SoundEvent ENTITY_VILLAGER_HUSK_STEP = register("entity.villager_husk.step");
	//Parrot Imitations
	public static final SoundEvent ENTITY_PARROT_IMITATE_DROWNED_VILLAGER = register("entity.parrot.imitate.drowned_villager");
	public static final SoundEvent ENTITY_PARROT_IMITATE_VILLAGER_HUSK = register("entity.parrot.imitate.villager_husk");

	private static SoundEvent register(String path) {
		Identifier id = Identifier.of(ZombieVillagerVariants.MOD_ID, path);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	public static final EntityType<DrownedVillagerEntity> DROWNED_VILLAGER = register(
			"drowned_villager",
			EntityType.Builder.create(DrownedVillagerEntity::new, SpawnGroup.MONSTER)
					.dimensions(0.6F, 1.95F)
					.passengerAttachments(2.125F)
					.vehicleAttachment(-0.7F)
					.eyeHeight(1.74F)
					.maxTrackingRange(8)
					.notAllowedInPeaceful()
	);
	public static final EntityType<VillagerHuskEntity> VILLAGER_HUSK = register(
			"villager_husk",
			EntityType.Builder.create(VillagerHuskEntity::new, SpawnGroup.MONSTER)
					.dimensions(0.6F, 1.95F)
					.passengerAttachments(2.125F)
					.vehicleAttachment(-0.7F)
					.eyeHeight(1.74F)
					.maxTrackingRange(8)
					.notAllowedInPeaceful()
	);
	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> type) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, name));
		EntityType<T> entityType = type.build(key);
		Registry.register(Registries.ENTITY_TYPE, key, entityType);
		return entityType;
	}


	public static final Item DROWNED_VILLAGER_SPAWN_EGG = register("drowned_villager_spawn_egg", SpawnEggItem::new, new Item.Settings().spawnEgg(DROWNED_VILLAGER));
	public static final Item VILLAGER_HUSK_SPAWN_EGG = register("villager_husk_spawn_egg", SpawnEggItem::new, new Item.Settings().spawnEgg(VILLAGER_HUSK));
	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
		Item item = itemFactory.apply(settings.registryKey(key));
		Registry.register(Registries.ITEM, key, item);
		return item;
	}

	@Override
	public void onInitialize() {
		//##########################################
		//#                                        #
		//#            Drowned Villager            #
		//#                                        #
		//##########################################
		//Attributes
		FabricDefaultAttributeRegistry.register(DROWNED_VILLAGER, DrownedVillagerEntity.createDrownedAttributes());
		//Spawning
		SpawnRestriction.register(DROWNED_VILLAGER, SpawnLocationTypes.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DrownedVillagerEntity::canDrownedVillagerSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.DRIPSTONE_CAVES),
				SpawnGroup.MONSTER, DROWNED_VILLAGER, 5, 1, 1);
		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN,
						BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN,
						BiomeKeys.WARM_OCEAN),
				SpawnGroup.MONSTER, DROWNED_VILLAGER, 1, 1, 1);
		//Items
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(itemGroup -> itemGroup.add(DROWNED_VILLAGER_SPAWN_EGG));

		//#########################################
		//#                                       #
		//#             Villager Husk             #
		//#                                       #
		//#########################################
		//Attributes
		FabricDefaultAttributeRegistry.register(VILLAGER_HUSK, VillagerHuskEntity.createZombieAttributes());
		//Spawning
		SpawnRestriction.register(VILLAGER_HUSK, SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VillagerHuskEntity::canSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.DESERT),
				SpawnGroup.MONSTER, VILLAGER_HUSK, 4, 1, 1);
		//Items
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(itemGroup -> itemGroup.add(VILLAGER_HUSK_SPAWN_EGG));
	}
}