package fun.wich.client;

import fun.wich.DrownedVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedVillagerEntityRenderer extends BipedEntityRenderer<DrownedVillagerEntity, ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/drowned_villager/drowned_villager.png");

	public DrownedVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(
				context,
				new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)),
				new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY)),
				0.5F,
				VillagerEntityRenderer.HEAD_TRANSFORMATION
		);
		this.addFeature(
				new ArmorFeatureRenderer<>(
						this,
						EquipmentModelData.mapToEntityModel(EntityModelLayers.ZOMBIE_VILLAGER_EQUIPMENT, context.getEntityModels(), ZombieVillagerEntityModel::new),
						EquipmentModelData.mapToEntityModel(EntityModelLayers.ZOMBIE_VILLAGER_BABY_EQUIPMENT, context.getEntityModels(), ZombieVillagerEntityModel::new),
						context.getEquipmentRenderer()
				)
		);
		this.addFeature(
				new VillagerClothingFeatureRenderer<>(
						this,
						context.getResourceManager(),
						"zombie_villager",
						new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_NO_HAT)),
						new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_NO_HAT))
				)
		);
		this.addFeature(new DrownedVillagerOverlayFeatureRenderer(this, context.getEntityModels()));
	}

	public Identifier getTexture(ZombieVillagerRenderState zombieVillagerRenderState) {
		return TEXTURE;
	}

	public ZombieVillagerRenderState createRenderState() {
		return new ZombieVillagerRenderState();
	}

	public void updateRenderState(DrownedVillagerEntity entity, ZombieVillagerRenderState zombieVillagerRenderState, float f) {
		super.updateRenderState(entity, zombieVillagerRenderState, f);
		zombieVillagerRenderState.convertingInWater = entity.isConverting() || entity.isConvertingInWater();
		zombieVillagerRenderState.villagerData = entity.getVillagerData();
		zombieVillagerRenderState.attacking = entity.isAttacking();
	}

	protected boolean isShaking(ZombieVillagerRenderState zombieVillagerRenderState) {
		return super.isShaking(zombieVillagerRenderState) || zombieVillagerRenderState.convertingInWater;
	}

	@Override
	protected BipedEntityModel.ArmPose getArmPose(DrownedVillagerEntity drownedEntity, Arm arm) {
		ItemStack itemStack = drownedEntity.getStackInArm(arm);
		if (drownedEntity.getMainArm() == arm && drownedEntity.isAttacking() && itemStack.isOf(Items.TRIDENT)) {
			return BipedEntityModel.ArmPose.THROW_SPEAR;
		}
		return BipedEntityModel.ArmPose.EMPTY;
	}
}
