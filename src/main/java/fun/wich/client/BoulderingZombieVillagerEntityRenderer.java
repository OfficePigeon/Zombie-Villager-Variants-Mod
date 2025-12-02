package fun.wich.client;

import fun.wich.BoulderingZombieVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BoulderingZombieVillagerEntityRenderer extends ExtendedZombieVillagerEntityRenderer<BoulderingZombieVillagerEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/bouldering_zombie_villager/bouldering_zombie_villager.png");
	public BoulderingZombieVillagerEntityRenderer(EntityRendererFactory.Context context) { super(context); }
	@Override public Identifier getTexture(BoulderingZombieVillagerEntity zombieVillagerRenderState) { return TEXTURE; }
}