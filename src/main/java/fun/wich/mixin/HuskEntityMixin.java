package fun.wich.mixin;

import fun.wich.ZombieVillagerVariants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HuskEntity.class)
public class HuskEntityMixin extends ZombieEntity {
	public HuskEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) { super(entityType, world); }
	@Override
	public boolean infectVillager(ServerWorld world, VillagerEntity villager) {
		ZombieVillagerEntity zombieVillagerEntity = villager.convertTo(ZombieVillagerVariants.VILLAGER_HUSK, EntityConversionContext.create(villager, true, true), zombieVillager -> {
					zombieVillager.initialize(world, world.getLocalDifficulty(zombieVillager.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true));
					zombieVillager.setVillagerData(villager.getVillagerData());
					zombieVillager.setGossip(villager.getGossip().copy());
					zombieVillager.setOfferData(villager.getOffers().copy());
					zombieVillager.setExperience(villager.getExperience());
					if (!this.isSilent()) {
						world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
					}
				}
		);
		return zombieVillagerEntity != null;
	}
}
