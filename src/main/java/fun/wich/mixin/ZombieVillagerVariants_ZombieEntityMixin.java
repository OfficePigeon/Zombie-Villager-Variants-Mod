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
public abstract class ZombieVillagerVariants_ZombieEntityMixin extends HostileEntity {
	protected ZombieVillagerVariants_ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) { super(entityType, world); }
	@Inject(method="infectVillager", at=@At("HEAD"), cancellable=true)
	public void Mixin_InfectVillager(ServerWorld world, VillagerEntity villager, CallbackInfoReturnable<Boolean> cir) {
		String from = this.getType().getTranslationKey();
		if (from == null) return;
		EntityType<? extends ZombieVillagerEntity> to = null;
		if (from.endsWith(".drowned") ) to = ZombieVillagerVariants.DROWNED_VILLAGER;
		else if (from.endsWith(".husk")) to = ZombieVillagerVariants.VILLAGER_HUSK;
		else if (from.endsWith(".frozen_zombie") ) to = ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER;
		else if (from.endsWith(".jungle_zombie")) to = ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER;
		else if (from.endsWith(".lobber_zombie")) to = ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER;
		else if (from.endsWith(".bouldering_zombie")) to = ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER;
		if (to != null) cir.setReturnValue(ExtendedZombieVillagerEntity.InfectVillager(world, villager, to));
	}
}