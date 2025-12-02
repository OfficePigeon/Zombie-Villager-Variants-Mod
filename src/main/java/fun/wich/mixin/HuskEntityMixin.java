package fun.wich.mixin;

import fun.wich.ExtendedZombieVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HuskEntity.class)
public class HuskEntityMixin extends ZombieEntity {
	public HuskEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) { super(entityType, world); }
	@Override
	public boolean infectVillager(ServerWorld world, VillagerEntity villager) {
		return ExtendedZombieVillagerEntity.InfectVillager(world, villager, ZombieVillagerVariants.VILLAGER_HUSK);
	}
}