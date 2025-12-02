package fun.wich;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class FrozenZombieVillagerEntity extends ExtendedZombieVillagerEntity implements RangedAttackMob {
	public FrozenZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) { super(entityType, world); }
	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(2, new FrozenZombieVillagerAttackGoal(this, 1.0, false));
		this.goalSelector.add(2, new SlowingProjectileAttackGoal(this, 1.25, 20, 10.0f));
		this.initSharedZombieVillagerGoals();
	}
	public static class FrozenZombieVillagerAttackGoal extends ZombieAttackGoal {
		public FrozenZombieVillagerAttackGoal(ZombieEntity zombie, double speed, boolean pauseWhenMobIdle) { super(zombie, speed, pauseWhenMobIdle); }
		@Override
		public boolean canStart() { return super.canStart() && IsTargetSlowed(this.mob.getTarget()); }
		@Override
		public boolean shouldContinue() { return super.shouldContinue() && IsTargetSlowed(this.mob.getTarget()); }
	}
	public static boolean IsTargetSlowed(LivingEntity target) {
		return target != null && (target.hasStatusEffect(StatusEffects.SLOWNESS) || target.inPowderSnow || target.wasInPowderSnow);
	}
	public static class SlowingProjectileAttackGoal extends ProjectileAttackGoal {
		protected final MobEntity mob;
		public SlowingProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
			super(mob, mobSpeed, intervalTicks, maxShootRange);
			this.mob = (MobEntity)mob;
		}
		@Override
		public boolean canStart() { return super.canStart() && !IsTargetSlowed(this.mob.getTarget()); }
		@Override
		public boolean shouldContinue() { return super.shouldContinue() && !IsTargetSlowed(this.mob.getTarget()); }
	}
	public static boolean canSpawn(EntityType<FrozenZombieVillagerEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		BlockPos blockPos = pos;
		while (true) { if (!world.getBlockState(blockPos = blockPos.up()).isOf(Blocks.POWDER_SNOW)) break; }
		return canSpawnInDark(type, world, spawnReason, pos, random) && (spawnReason == SpawnReason.SPAWNER || world.isSkyVisible(blockPos.down()));
	}
	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		double d = target.getX() - this.getX();
		double e = target.getEyeY() - 1.1;
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f) * 0.2;
		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
			ProjectileEntity entity = new FrozenZombieVillagerSnowballEntity(serverWorld, this);
			entity.setVelocity(d, e + g - entity.getY(), f, 1.6F, 12.0F);
			serverWorld.spawnEntity(entity);
		}
		this.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	}
	@Override
	public SoundEvent GetCureSound() { return ZombieVillagerVariants.ENTITY_FROZEN_ZOMBIE_VILLAGER_CURE; }
	@Override
	public SoundEvent getAmbientSound() { return ZombieVillagerVariants.ENTITY_FROZEN_ZOMBIE_VILLAGER_AMBIENT; }
	@Override
	public SoundEvent getHurtSound(DamageSource source) { return ZombieVillagerVariants.ENTITY_FROZEN_ZOMBIE_VILLAGER_HURT; }
	@Override
	public SoundEvent getDeathSound() { return ZombieVillagerVariants.ENTITY_FROZEN_ZOMBIE_VILLAGER_DEATH; }
	@Override
	public SoundEvent getStepSound() { return ZombieVillagerVariants.ENTITY_FROZEN_ZOMBIE_VILLAGER_STEP; }
	@Override
	public RegistryEntry<StatusEffect> GetStatusEffectOnHit() { return StatusEffects.SLOWNESS; }
	@Override
	public boolean canFreeze() { return false; }
	@Override
	protected boolean canConvertInWater() { return true; }
	@Override
	protected void convertInWater() {
		ConvertToZombieVillagerEntity(this, EntityType.ZOMBIE_VILLAGER, ZombieVillagerVariants.ENTITY_FROZEN_ZOMBIE_VILLAGER_CONVERTED_TO_ZOMBIE_VILLAGER);
	}
}