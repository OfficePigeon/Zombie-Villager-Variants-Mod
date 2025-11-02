package fun.wich.client;

import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
	public static final EntityModelLayer DROWNED_VILLAGER_OUTER = MakeModelLayer("drowned_villager", "outer");
	public static final EntityModelLayer DROWNED_VILLAGER_BABY_OUTER = MakeModelLayer("drowned_villager_baby", "outer");
	private static EntityModelLayer MakeModelLayer(String id, String layer) {
		return new EntityModelLayer(Identifier.of(ZombieVillagerVariants.MOD_ID, id), layer);
	}

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(DROWNED_VILLAGER_OUTER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(0.25F));
		EntityModelLayerRegistry.registerModelLayer(DROWNED_VILLAGER_BABY_OUTER, () -> DilatedZombieVillagerEntityModel.getDilatedTexturedModelData(0.25F).transform(BipedEntityModel.BABY_TRANSFORMER));
		EntityRendererFactories.register(ZombieVillagerVariants.DROWNED_VILLAGER, DrownedVillagerEntityRenderer::new);

		EntityRendererFactories.register(ZombieVillagerVariants.VILLAGER_HUSK, VillagerHuskEntityRenderer::new);
	}
}
