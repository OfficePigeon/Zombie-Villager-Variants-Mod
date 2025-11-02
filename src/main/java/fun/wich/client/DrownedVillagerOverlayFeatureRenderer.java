package fun.wich.client;

import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class DrownedVillagerOverlayFeatureRenderer extends FeatureRenderer<ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> {
	public static final Identifier SKIN = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/drowned_villager/drowned_villager_outer_layer.png");
	private final DilatedZombieVillagerEntityModel model;
	private final DilatedZombieVillagerEntityModel babyModel;

	public DrownedVillagerOverlayFeatureRenderer(FeatureRendererContext<ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> context, LoadedEntityModels loader) {
		super(context);
		this.model = new DilatedZombieVillagerEntityModel(loader.getModelPart(ModClient.DROWNED_VILLAGER_OUTER));
		this.babyModel = new DilatedZombieVillagerEntityModel(loader.getModelPart(ModClient.DROWNED_VILLAGER_BABY_OUTER));
	}

	@Override
	public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, ZombieVillagerRenderState zombieEntityRenderState, float f, float g) {
		DilatedZombieVillagerEntityModel drownedEntityModel = zombieEntityRenderState.baby ? this.babyModel : this.model;
		render(drownedEntityModel, SKIN, matrixStack, orderedRenderCommandQueue, i, zombieEntityRenderState, -1, 1);
	}
}
