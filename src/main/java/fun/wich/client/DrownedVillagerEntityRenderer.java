package fun.wich.client;

import fun.wich.DrownedVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedVillagerEntityRenderer extends ExtendedZombieVillagerEntityRenderer<DrownedVillagerEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/drowned_villager/drowned_villager.png");
	public static final Identifier SKIN = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/drowned_villager/drowned_villager_outer_layer.png");
	public DrownedVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.addFeature(new ZombieVillagerOverlayFeatureRenderer<>(this, context.getModelLoader(), TEXTURE, ZombieVillagerVariantsClient.MULTILAYER_ZOMBIE_VILLAGER_INNER));
	}
	@Override public Identifier getTexture(DrownedVillagerEntity zombieVillagerRenderState) { return SKIN; }
}