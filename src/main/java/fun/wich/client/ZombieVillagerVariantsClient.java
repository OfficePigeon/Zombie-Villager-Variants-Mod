package fun.wich.client;

import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerVariantsClient implements ClientModInitializer {
	public static final EntityModelLayer MULTILAYER_ZOMBIE_VILLAGER_INNER = MakeModelLayer("inner");
	public static final EntityModelLayer MULTILAYER_ZOMBIE_VILLAGER_OUTER = MakeModelLayer("outer");
	private static EntityModelLayer MakeModelLayer(String name) {
		return new EntityModelLayer(Identifier.of(ZombieVillagerVariants.MOD_ID, "multilayer_zombie_villager"), name);
	}
	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(MULTILAYER_ZOMBIE_VILLAGER_INNER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(-0.25F));
		EntityModelLayerRegistry.registerModelLayer(MULTILAYER_ZOMBIE_VILLAGER_OUTER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(0.25F));
		EntityRendererRegistry.register(ZombieVillagerVariants.DROWNED_VILLAGER, DrownedVillagerEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.VILLAGER_HUSK, VillagerHuskEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER, FrozenZombieVillagerEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER_SNOWBALL, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER, JungleZombieVillagerEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER, LobberZombieVillagerEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER_THROWN_FLESH, FlyingItemEntityRenderer::new);
		EntityRendererRegistry.register(ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER, BoulderingZombieVillagerEntityRenderer::new);
	}
}