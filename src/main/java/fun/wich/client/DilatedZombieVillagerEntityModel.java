package fun.wich.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;

public class DilatedZombieVillagerEntityModel extends ZombieVillagerEntityModel<ZombieVillagerRenderState> {
	public DilatedZombieVillagerEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getDilatedTexturedModelData(float dilation) {
		Dilation DILATION = new Dilation(dilation);
		ModelData modelData = BipedEntityModel.getModelData(DILATION, 0.0f);
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, new ModelPartBuilder().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f).uv(24, 0).cuboid(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, DILATION), ModelTransform.NONE);
		ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new Dilation(0.5f)), ModelTransform.NONE);
		modelPartData3.addChild(EntityModelPartNames.HAT_RIM, ModelPartBuilder.create().uv(30, 47).cuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), ModelTransform.rotation(-1.5707964f, 0.0f, 0.0f));
		modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, DILATION).uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 20.0f, 6.0f, new Dilation(0.05f)), ModelTransform.NONE);
		modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(44, 22).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, DILATION), ModelTransform.origin(-5.0f, 2.0f, 0.0f));
		modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, DILATION), ModelTransform.origin(5.0f, 2.0f, 0.0f));
		modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, DILATION), ModelTransform.origin(-2.0f, 12.0f, 0.0f));
		modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, DILATION), ModelTransform.origin(2.0f, 12.0f, 0.0f));
		return TexturedModelData.of(modelData, 64, 64);
	}
}
