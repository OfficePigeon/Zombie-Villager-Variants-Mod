package fun.wich.client;

import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerVariantsClient implements ClientModInitializer {
	public static final EntityModelLayer MULTILAYER_ZOMBIE_VILLAGER_INNER = MakeModelLayer("multilayer_zombie_villager", "inner");
	public static final EntityModelLayer MULTILAYER_ZOMBIE_VILLAGER_BABY_INNER = MakeModelLayer("multilayer_zombie_villager_baby", "inner");
	public static final EntityModelLayer MULTILAYER_ZOMBIE_VILLAGER_OUTER = MakeModelLayer("multilayer_zombie_villager", "outer");
	public static final EntityModelLayer MULTILAYER_ZOMBIE_VILLAGER_BABY_OUTER = MakeModelLayer("multilayer_zombie_villager_baby", "outer");
	private static EntityModelLayer MakeModelLayer(String id, String name) {
		return new EntityModelLayer(Identifier.of(ZombieVillagerVariants.MOD_ID, id), name);
	}
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(MULTILAYER_ZOMBIE_VILLAGER_INNER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(-0.25F));
		EntityModelLayerRegistry.registerModelLayer(MULTILAYER_ZOMBIE_VILLAGER_BABY_INNER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(-0.25F).transform(BipedEntityModel.BABY_TRANSFORMER));
		EntityModelLayerRegistry.registerModelLayer(MULTILAYER_ZOMBIE_VILLAGER_OUTER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(0.25F));
		EntityModelLayerRegistry.registerModelLayer(MULTILAYER_ZOMBIE_VILLAGER_BABY_OUTER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(0.25F).transform(BipedEntityModel.BABY_TRANSFORMER));
		EntityRendererFactories.register(ZombieVillagerVariants.DROWNED_VILLAGER, DrownedVillagerEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.VILLAGER_HUSK, VillagerHuskEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER, FrozenZombieVillagerEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER_SNOWBALL, FlyingItemEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER, JungleZombieVillagerEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER, LobberZombieVillagerEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER_THROWN_FLESH, FlyingItemEntityRenderer::new);
		EntityRendererFactories.register(ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER, BoulderingZombieVillagerEntityRenderer::new);
	}
}