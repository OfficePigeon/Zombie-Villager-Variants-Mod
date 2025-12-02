package fun.wich.client;

import fun.wich.LobberZombieVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LobberZombieVillagerEntityRenderer extends ExtendedZombieVillagerEntityRenderer<LobberZombieVillagerEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/lobber_zombie_villager/lobber_zombie_villager.png");
	public LobberZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.features.removeIf(feature -> feature instanceof HeldItemFeatureRenderer);
		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()) {
			@Override
			public void render(MatrixStack matrixStack, VertexConsumerProvider orderedRenderCommandQueue, int light, LobberZombieVillagerEntity state, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
				if (state.IsAttackingRanged()) super.render(matrixStack, orderedRenderCommandQueue, light, state, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
			}
		});
	}
	@Override public Identifier getTexture(LobberZombieVillagerEntity zombieVillagerRenderState) { return TEXTURE; }
}