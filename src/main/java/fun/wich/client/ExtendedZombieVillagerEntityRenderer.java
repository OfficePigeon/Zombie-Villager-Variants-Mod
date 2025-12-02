package fun.wich.client;

import fun.wich.ExtendedZombieVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;

@Environment(EnvType.CLIENT)
public abstract class ExtendedZombieVillagerEntityRenderer<E extends ExtendedZombieVillagerEntity> extends BipedEntityRenderer<E, ZombieVillagerEntityModel<E>> {
	public ExtendedZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), 0.5F);
		this.addFeature(new ArmorFeatureRenderer<>(this, new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), context.getModelManager()));
		this.addFeature(new VillagerClothingFeatureRenderer<>(this, context.getResourceManager(), "zombie_villager"));
	}
	@Override protected boolean isShaking(E state) { return super.isShaking(state) || state.isConvertingInWater(); }
}