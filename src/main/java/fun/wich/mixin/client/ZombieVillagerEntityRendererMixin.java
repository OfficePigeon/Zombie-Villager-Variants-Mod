package fun.wich.mixin.client;

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
	private void AllowShakingWhileConverting(ZombieVillagerEntity zombieVillagerEntity, ZombieVillagerRenderState zombieVillagerRenderState, float f, CallbackInfo ci) {
		zombieVillagerRenderState.convertingInWater = zombieVillagerEntity.isConverting() || zombieVillagerEntity.isConvertingInWater();
	}
}
