package fun.wich.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class ZombieVillagerOverlayFeatureRenderer extends FeatureRenderer<ExtendedZombieVillagerRenderState, ZombieVillagerEntityModel<ExtendedZombieVillagerRenderState>> {
	private final Identifier skin;
	private final DilatedZombieVillagerEntityModel model;
	private final DilatedZombieVillagerEntityModel babyModel;
	public ZombieVillagerOverlayFeatureRenderer(FeatureRendererContext<ExtendedZombieVillagerRenderState, ZombieVillagerEntityModel<ExtendedZombieVillagerRenderState>> context, LoadedEntityModels loader, Identifier skin) {
		this(context, loader, skin, ZombieVillagerVariantsClient.MULTILAYER_ZOMBIE_VILLAGER_OUTER, ZombieVillagerVariantsClient.MULTILAYER_ZOMBIE_VILLAGER_BABY_OUTER);
	}
	public ZombieVillagerOverlayFeatureRenderer(FeatureRendererContext<ExtendedZombieVillagerRenderState, ZombieVillagerEntityModel<ExtendedZombieVillagerRenderState>> context, LoadedEntityModels loader, Identifier skin, EntityModelLayer layer, EntityModelLayer babyLayer) {
		super(context);
		this.skin = skin;
		this.model = new DilatedZombieVillagerEntityModel(loader.getModelPart(layer));
		this.babyModel = new DilatedZombieVillagerEntityModel(loader.getModelPart(babyLayer));
	}
	@Override
	public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, ExtendedZombieVillagerRenderState state, float headYaw, float headPitch) {
		render(state.baby ? this.babyModel : this.model, this.skin, matrixStack, orderedRenderCommandQueue, light, state, -1, 1);
	}
}