package fun.wich.client;

import fun.wich.ZombieVillagerVariants;
import fun.wich.FrozenZombieVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FrozenZombieVillagerEntityRenderer extends ExtendedZombieVillagerEntityRenderer<FrozenZombieVillagerEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/frozen_zombie_villager/frozen_zombie_villager.png");
	public static final Identifier SKIN = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/frozen_zombie_villager/frozen_zombie_villager_outer_layer.png");
	public FrozenZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.addFeature(new ZombieVillagerOverlayFeatureRenderer(this, context.getEntityModels(), SKIN));
	}
	@Override public Identifier getTexture(ExtendedZombieVillagerRenderState state) { return TEXTURE; }
}