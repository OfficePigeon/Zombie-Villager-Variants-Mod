package fun.wich.client;

import fun.wich.VillagerHuskEntity;
import fun.wich.ZombieVillagerVariants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VillagerHuskEntityRenderer extends BipedEntityRenderer<VillagerHuskEntity, ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> {
	public static final Identifier TEXTURE = Identifier.of(ZombieVillagerVariants.MOD_ID,"textures/entity/villager_husk/villager_husk.png");

	public VillagerHuskEntityRenderer(EntityRendererFactory.Context context) {
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
	}

	public Identifier getTexture(ZombieVillagerRenderState zombieVillagerRenderState) {
		return TEXTURE;
	}

	public ZombieVillagerRenderState createRenderState() {
		return new ZombieVillagerRenderState();
	}

	public void updateRenderState(VillagerHuskEntity entity, ZombieVillagerRenderState zombieVillagerRenderState, float f) {
		super.updateRenderState(entity, zombieVillagerRenderState, f);
		zombieVillagerRenderState.convertingInWater = entity.isConverting() || entity.isConvertingInWater();
		zombieVillagerRenderState.villagerData = entity.getVillagerData();
		zombieVillagerRenderState.attacking = entity.isAttacking();
	}

	protected boolean isShaking(ZombieVillagerRenderState zombieVillagerRenderState) {
		return super.isShaking(zombieVillagerRenderState) || zombieVillagerRenderState.convertingInWater;
	}
}
