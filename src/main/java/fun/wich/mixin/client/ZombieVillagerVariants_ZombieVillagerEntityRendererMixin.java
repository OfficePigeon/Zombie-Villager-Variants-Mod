package fun.wich.mixin.client;

import fun.wich.ZombieVillagerFreezeTracker;
import net.minecraft.client.render.entity.ZombieVillagerEntityRenderer;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntityRenderer.class)
public class ZombieVillagerVariants_ZombieVillagerEntityRendererMixin {
	@Inject(method="isShaking(Lnet/minecraft/entity/mob/ZombieVillagerEntity;)Z", at=@At("HEAD"), cancellable=true)
	protected void AllowShakingWhileConverting(ZombieVillagerEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity.isConverting() || entity.isConvertingInWater()) cir.setReturnValue(true);
		if (entity instanceof ZombieVillagerFreezeTracker freeze && freeze.ZombieVillagerFreezeTracker_IsShaking()) cir.setReturnValue(true);
	}
}