package fun.wich.client;

import fun.wich.VillagerHuskEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerHuskEntityRenderer extends ExtendedZombieVillagerEntityRenderer<VillagerHuskEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/villager_husk/villager_husk.png");
	public VillagerHuskEntityRenderer(EntityRendererFactory.Context context) { super(context); }
	@Override public Identifier getTexture(VillagerHuskEntity zombieVillagerRenderState) { return TEXTURE; }
}