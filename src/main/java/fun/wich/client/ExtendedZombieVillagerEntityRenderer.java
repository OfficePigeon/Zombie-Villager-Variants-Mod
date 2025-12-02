package fun.wich.client;

import fun.wich.ExtendedZombieVillagerEntity;
import fun.wich.LobberZombieVillagerEntity;
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

@Environment(EnvType.CLIENT)
public abstract class ExtendedZombieVillagerEntityRenderer<E extends ExtendedZombieVillagerEntity> extends BipedEntityRenderer<E, ExtendedZombieVillagerRenderState, ZombieVillagerEntityModel<ExtendedZombieVillagerRenderState>> {
	public ExtendedZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5F, VillagerEntityRenderer.HEAD_TRANSFORMATION);
		this.addFeature(new ArmorFeatureRenderer<>(this, EquipmentModelData.mapToEntityModel(EntityModelLayers.ZOMBIE_VILLAGER_EQUIPMENT, context.getEntityModels(), ZombieVillagerEntityModel::new), EquipmentModelData.mapToEntityModel(EntityModelLayers.ZOMBIE_VILLAGER_BABY_EQUIPMENT, context.getEntityModels(), ZombieVillagerEntityModel::new), context.getEquipmentRenderer()));
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "zombie_villager", new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_NO_HAT)), new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_NO_HAT))));
	}
	@Override public ExtendedZombieVillagerRenderState createRenderState() { return new ExtendedZombieVillagerRenderState(); }
	@Override protected boolean isShaking(ExtendedZombieVillagerRenderState state) { return super.isShaking(state) || state.convertingInWater; }
	@Override
	public void updateRenderState(E entity, ExtendedZombieVillagerRenderState state, float f) {
		super.updateRenderState(entity, state, f);
		state.convertingInWater = entity.isConverting() || entity.isConvertingInWater();
		state.villagerData = entity.getVillagerData();
		state.attacking = entity.isAttacking();
		if (entity instanceof LobberZombieVillagerEntity lobber) state.attackingRanged = lobber.IsAttackingRanged();
	}
}