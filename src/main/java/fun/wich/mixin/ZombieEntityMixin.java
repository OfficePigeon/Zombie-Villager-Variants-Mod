package fun.wich.mixin;

import fun.wich.ExtendedZombieVillagerEntity;
import fun.wich.ZombieVillagerVariants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
	protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) { super(entityType, world); }
	@Inject(method="infectVillager", at=@At("HEAD"), cancellable=true)
	public void Mixin_InfectVillager(ServerWorld world, VillagerEntity villager, CallbackInfoReturnable<Boolean> cir) {
		String entityType = this.getType().getTranslationKey();
		if (entityType == null) return;
		EntityType<? extends ZombieVillagerEntity> type = null;
		if (entityType.contains("." + ZombieVillagerVariants.MOD_ID + ".")) {
			if (entityType.endsWith(".frozen_zombie") ) type = ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER;
			else if (entityType.endsWith(".jungle_zombie")) type = ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER;
			else if (entityType.endsWith(".lobber_zombie")) type = ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER;
			else if (entityType.endsWith(".bouldering_zombie")) type = ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER;
		}
		else if (entityType.contains(".earthtojavamobs.") || entityType.contains(".minecraft_earth_mod.")) {
			if (entityType.endsWith(".lobber_zombie")) type = ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER;
			else if (entityType.endsWith(".bouldering_zombie")) type = ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER;
		}
		if (type != null) cir.setReturnValue(ExtendedZombieVillagerEntity.InfectVillager(world, villager, type));
	}
}