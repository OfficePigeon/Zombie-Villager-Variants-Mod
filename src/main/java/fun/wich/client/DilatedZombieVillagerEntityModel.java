package fun.wich.client;

import fun.wich.ExtendedZombieVillagerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;

public class DilatedZombieVillagerEntityModel<E extends ExtendedZombieVillagerEntity> extends ZombieVillagerEntityModel<E> {
	public DilatedZombieVillagerEntityModel(ModelPart modelPart) { super(modelPart); }
	public static TexturedModelData getDilatedTexturedModelData(float dilation) {
		Dilation DILATION = new Dilation(dilation);
		ModelData modelData = BipedEntityModel.getModelData(DILATION, 0);
		ModelPartData root = modelData.getRoot();
		root.addChild(EntityModelPartNames.HEAD, new ModelPartBuilder().uv(0, 0).cuboid(-4, -10, -4, 8, 10, 8, DILATION).uv(24, 0).cuboid(-1, -3, -6, 2, 4, 2, DILATION), ModelTransform.NONE);
		ModelPartData hat = root.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4, -10, -4, 8, 10, 8, DILATION.add(0.5f)), ModelTransform.NONE);
		hat.addChild(EntityModelPartNames.HAT_RIM, ModelPartBuilder.create().uv(30, 47).cuboid(-8, -8, -6, 16, 16, 1, DILATION), ModelTransform.rotation(-1.5707964f, 0, 0));
		root.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 20).cuboid(-4, 0, -3, 8, 12, 6, DILATION).uv(0, 38).cuboid(-4, 0, -3, 8, 20, 6, DILATION.add(0.05f)), ModelTransform.NONE);
		root.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(44, 22).cuboid(-3, -2, -2, 4, 12, 4, DILATION), ModelTransform.pivot(-5, 2, 0));
		root.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(-1, -2, -2, 4, 12, 4, DILATION), ModelTransform.pivot(5, 2, 0));
		root.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 22).cuboid(-2, 0, -2, 4, 12, 4, DILATION), ModelTransform.pivot(-2, 12, 0));
		root.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2, 0, -2, 4, 12, 4, DILATION), ModelTransform.pivot(2, 12, 0));
		return TexturedModelData.of(modelData, 64, 64);
	}
}