package fun.wich.client;

import fun.wich.LobberZombieVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
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
		this.addFeature(new HeldItemFeatureRenderer<>(this) {
			public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, ExtendedZombieVillagerRenderState state, float f, float g) {
				if (state.attackingRanged) super.render(matrixStack, orderedRenderCommandQueue, i, state, f, g);
			}
		});
	}
	public Identifier getTexture(ExtendedZombieVillagerRenderState zombieVillagerRenderState) { return TEXTURE; }
}