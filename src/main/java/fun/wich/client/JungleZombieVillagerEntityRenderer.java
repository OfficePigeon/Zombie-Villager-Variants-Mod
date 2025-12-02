package fun.wich.client;

import fun.wich.JungleZombieVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JungleZombieVillagerEntityRenderer extends ExtendedZombieVillagerEntityRenderer<JungleZombieVillagerEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/jungle_zombie_villager/jungle_zombie_villager.png");
	public static final Identifier SKIN = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/jungle_zombie_villager/jungle_zombie_villager_outer_layer.png");
	public JungleZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.addFeature(new ZombieVillagerOverlayFeatureRenderer(this, context.getEntityModels(), SKIN));
	}
	@Override
	public Identifier getTexture(ExtendedZombieVillagerRenderState zombieVillagerRenderState) { return TEXTURE; }
}