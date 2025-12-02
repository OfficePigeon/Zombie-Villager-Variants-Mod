package fun.wich.mixin;

import fun.wich.ExtendedZombieVillagerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerHostilesSensor.class)
public class VillagerHostilesSensorMixin {
	@Inject(method="matches", at = @At("HEAD"), cancellable = true)
	protected void Matches(ServerWorld world, LivingEntity entity, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
		double distance = entity.squaredDistanceTo(target);
		if (target instanceof ExtendedZombieVillagerEntity && distance <= 64) cir.setReturnValue(true);
	}
}