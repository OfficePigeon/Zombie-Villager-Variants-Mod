package fun.wich.client;

import fun.wich.ExtendedZombieVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class ZombieVillagerOverlayFeatureRenderer<E extends ExtendedZombieVillagerEntity> extends FeatureRenderer<E, ZombieVillagerEntityModel<E>> {
	private final Identifier skin;
	private final DilatedZombieVillagerEntityModel<E> model;
	public ZombieVillagerOverlayFeatureRenderer(FeatureRendererContext<E, ZombieVillagerEntityModel<E>> context, EntityModelLoader loader, Identifier skin) {
		this(context, loader, skin, ZombieVillagerVariantsClient.MULTILAYER_ZOMBIE_VILLAGER_OUTER);
	}
	public ZombieVillagerOverlayFeatureRenderer(FeatureRendererContext<E, ZombieVillagerEntityModel<E>> context, EntityModelLoader loader, Identifier skin, EntityModelLayer layer) {
		super(context);
		this.skin = skin;
		this.model = new DilatedZombieVillagerEntityModel<>(loader.getModelPart(layer));
	}
	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider orderedRenderCommandQueue, int light, E state, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		render(this.getContextModel(), this.model, skin, matrixStack, orderedRenderCommandQueue, light, state, limbAngle, limbDistance, animationProgress, headYaw, headPitch, tickDelta, -1);
	}
}