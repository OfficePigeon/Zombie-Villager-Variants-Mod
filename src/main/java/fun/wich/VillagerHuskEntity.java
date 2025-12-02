package fun.wich;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class VillagerHuskEntity extends ExtendedZombieVillagerEntity {
	public VillagerHuskEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) { super(entityType, world); }
	public static boolean canSpawn(EntityType<VillagerHuskEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return canSpawnInDark(type, world, spawnReason, pos, random) && (SpawnReason.isAnySpawner(spawnReason) || world.isSkyVisible(pos));
	}
	@Override
	public SoundEvent GetCureSound() { return ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_CURE; }
	@Override
	public boolean burnsInDaylight() { return false; }
	@Override
	public SoundEvent getAmbientSound() { return ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_AMBIENT; }
	@Override
	public SoundEvent getHurtSound(DamageSource source) { return ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_HURT; }
	@Override
	public SoundEvent getDeathSound() { return ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_DEATH; }
	@Override
	public SoundEvent getStepSound() { return ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_STEP; }
	@Override
	public RegistryEntry<StatusEffect> GetStatusEffectOnHit() { return StatusEffects.HUNGER; }
	@Override
	protected boolean canConvertInWater() { return true; }
	@Override
	protected void convertInWater() {
		ConvertToZombieVillagerEntity(this, EntityType.ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_CONVERTED_TO_ZOMBIE_VILLAGER);
	}
}