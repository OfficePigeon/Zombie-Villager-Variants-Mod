package fun.wich.mixin;

import fun.wich.ZombieVillagerVariants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(ParrotEntity.class)
public class ParrotEntityMixin {
	@Shadow @Final static Map<EntityType<?>, SoundEvent> MOB_SOUNDS;
	@Shadow private static SoundEvent getSound(EntityType<?> imitate) { return null; }
	@Unique
	private static final Predicate<MobEntity> CAN_IMITATE_NEW = new Predicate<>(){
		@Override
		public boolean test(MobEntity mobEntity) {
			if (mobEntity == null) return false;
			EntityType<?> type = mobEntity.getType();
			if (!MOB_SOUNDS.containsKey(type)) {
				if (type == ZombieVillagerVariants.DROWNED_VILLAGER) MOB_SOUNDS.put(ZombieVillagerVariants.DROWNED_VILLAGER, ZombieVillagerVariants.ENTITY_PARROT_IMITATE_DROWNED_VILLAGER);
				else if (type == ZombieVillagerVariants.VILLAGER_HUSK) MOB_SOUNDS.put(ZombieVillagerVariants.VILLAGER_HUSK, ZombieVillagerVariants.ENTITY_PARROT_IMITATE_VILLAGER_HUSK);
				else if (type == ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER) MOB_SOUNDS.put(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_PARROT_IMITATE_FROZEN_ZOMBIE_VILLAGER);
				else if (type == ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER) MOB_SOUNDS.put(ZombieVillagerVariants.JUNGLE_ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_PARROT_IMITATE_JUNGLE_ZOMBIE_VILLAGER);
				else if (type == ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER) MOB_SOUNDS.put(ZombieVillagerVariants.LOBBER_ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_PARROT_IMITATE_LOBBER_ZOMBIE_VILLAGER);
				else if (type == ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER) MOB_SOUNDS.put(ZombieVillagerVariants.BOULDERING_ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_PARROT_IMITATE_BOULDERING_ZOMBIE_VILLAGER);
			}
			return MOB_SOUNDS.containsKey(type);
		}
	};
	@Inject(method="imitateNearbyMob", at=@At("HEAD"), cancellable=true)
	private static void TryImitations(World world, Entity parrot, CallbackInfoReturnable<Boolean> cir) {
		MobEntity mobEntity;
		if (parrot.isAlive() && !parrot.isSilent() && world.random.nextInt(2) == 0) {
			List<MobEntity> list = world.getEntitiesByClass(MobEntity.class, parrot.getBoundingBox().expand(20.0), CAN_IMITATE_NEW);
			if (!list.isEmpty() && !(mobEntity = list.get(world.random.nextInt(list.size()))).isSilent()) {
				SoundEvent soundEvent = getSound(mobEntity.getType());
				world.playSound(null, parrot.getX(), parrot.getY(), parrot.getZ(), soundEvent, parrot.getSoundCategory(), 0.7f, ParrotEntity.getSoundPitch(world.random));
				cir.setReturnValue(true);
			}
		}
	}
}