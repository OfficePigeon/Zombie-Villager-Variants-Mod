package fun.wich.client;

import fun.wich.DrownedVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedVillagerEntityRenderer extends ExtendedZombieVillagerEntityRenderer<DrownedVillagerEntity> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/drowned_villager/drowned_villager.png");
	public static final Identifier SKIN = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/drowned_villager/drowned_villager_outer_layer.png");
	public DrownedVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.addFeature(new ZombieVillagerOverlayFeatureRenderer(this, context.getEntityModels(), TEXTURE, ZombieVillagerVariantsClient.MULTILAYER_ZOMBIE_VILLAGER_INNER, ZombieVillagerVariantsClient.MULTILAYER_ZOMBIE_VILLAGER_BABY_INNER));
	}
	@Override public Identifier getTexture(ExtendedZombieVillagerRenderState state) { return SKIN; }
	@Override
	protected BipedEntityModel.ArmPose getArmPose(DrownedVillagerEntity entity, Arm arm) {
		if (entity.getMainArm() == arm && entity.isAttacking() && entity.getStackInArm(arm).isOf(Items.TRIDENT)) return BipedEntityModel.ArmPose.THROW_SPEAR;
		return BipedEntityModel.ArmPose.EMPTY;
	}
}