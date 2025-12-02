package fun.wich.mixin.client;

import fun.wich.ZombieVillagerFreezeTracker;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieVillagerEntityRenderer.class)
public class ZombieVillagerEntityRendererMixin {
	@Inject(method="updateRenderState(Lnet/minecraft/entity/mob/ZombieVillagerEntity;Lnet/minecraft/client/render/entity/state/ZombieVillagerRenderState;F)V", at=@At("TAIL"))
	private void AllowShakingWhileConverting(ZombieVillagerEntity entity, ZombieVillagerRenderState state, float f, CallbackInfo ci) {
		state.convertingInWater = entity.isConverting() || entity.isConvertingInWater();
		if (entity instanceof ZombieVillagerFreezeTracker freeze && freeze.ZombieVillagerFreezeTracker_IsShaking()) state.shaking = true;
	}
}