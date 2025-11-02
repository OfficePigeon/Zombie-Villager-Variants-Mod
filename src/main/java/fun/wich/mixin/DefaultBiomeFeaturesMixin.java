package fun.wich.mixin;

import fun.wich.ZombieVillagerVariants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {
	@Inject(method="addMonsters", at=@At("HEAD"))
	private static void AddZombieOrDrownedVillagers(SpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight, boolean drowned, CallbackInfo ci) {
		builder.spawn(SpawnGroup.MONSTER, zombieVillagerWeight, new SpawnSettings.SpawnEntry(drowned ? ZombieVillagerVariants.DROWNED_VILLAGER : EntityType.ZOMBIE_VILLAGER, 1, 1));
	}

	@Redirect(method="addMonsters", at=@At(value="INVOKE", target="Lnet/minecraft/world/biome/SpawnSettings$Builder;spawn(Lnet/minecraft/entity/SpawnGroup;ILnet/minecraft/world/biome/SpawnSettings$SpawnEntry;)Lnet/minecraft/world/biome/SpawnSettings$Builder;"))
	private static SpawnSettings.Builder InjectZombieVillagersElsewhere(SpawnSettings.Builder instance, SpawnGroup spawnGroup, int weight, SpawnSettings.SpawnEntry entry) {
		return instance;
	}
}
