package fun.wich;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;

import java.util.EnumSet;

public class DrownedVillagerEntity extends ExtendedZombieVillagerEntity implements RangedAttackMob {
	protected boolean targetingUnderwater;
	public DrownedVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new DrownedMoveControl(this);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
	}
	public static DefaultAttributeContainer.Builder createDrownedAttributes() {
		return createZombieAttributes().add(EntityAttributes.STEP_HEIGHT, 1.0);
	}
	@Override
	protected EntityNavigation createNavigation(World world) { return new AmphibiousSwimNavigation(this, world); }
	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(1, new WanderAroundOnSurfaceGoal(this, 1.0));
		this.goalSelector.add(2, new TridentAttackGoal(this, 1.0, 40, 10.0f));
		this.goalSelector.add(2, new DrownedAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new LeaveWaterGoal(this, 1.0));
		this.goalSelector.add(6, new TargetAboveWaterGoal(this, 1.0, this.getEntityWorld().getSeaLevel()));
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this, DrownedVillagerEntity.class).setGroupRevenge(ZombifiedPiglinEntity.class));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (target, world) -> this.canDrownedAttackTarget(target)));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, AxolotlEntity.class, true, false));
		this.targetSelector.add(5, new ActiveTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData);
		if (this.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty() && world.getRandom().nextFloat() < 0.03f) {
			this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
			this.setDropGuaranteed(EquipmentSlot.OFFHAND);
		}
		return entityData;
	}
	public static boolean canDrownedVillagerSpawn(EntityType<DrownedVillagerEntity> ignoredType, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		if (!world.getFluidState(pos.down()).isIn(FluidTags.WATER) && !SpawnReason.isAnySpawner(spawnReason)) return false;
		else {
			RegistryEntry<Biome> registryEntry = world.getBiome(pos);
			boolean bl = world.getDifficulty() != Difficulty.PEACEFUL && (SpawnReason.isTrialSpawner(spawnReason) || isSpawnDark(world, pos, random)) && (SpawnReason.isAnySpawner(spawnReason) || world.getFluidState(pos).isIn(FluidTags.WATER));
			if (!bl || !SpawnReason.isAnySpawner(spawnReason) && spawnReason != SpawnReason.REINFORCEMENT) {
				if (registryEntry.isIn(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) return random.nextInt(15) == 0 && bl;
				else return random.nextInt(40) == 0 && isValidSpawnDepth(world, pos) && bl;
			}
			else return true;
		}
	}
	public static boolean isValidSpawnDepth(WorldAccess world, BlockPos pos) {
		return pos.getY() < world.getSeaLevel() - 5;
	}
	@Override
	public SoundEvent GetCureSound() { return ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_CURE; }
	@Override
	public SoundEvent getAmbientSound() {
		return this.isTouchingWater() ? ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_AMBIENT_WATER : ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_AMBIENT;
	}
	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return this.isTouchingWater() ? ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_HURT_WATER : ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_HURT;
	}
	@Override
	public SoundEvent getDeathSound() {
		return this.isTouchingWater() ? ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_DEATH_WATER : ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_DEATH;
	}
	@Override
	public SoundEvent getStepSound() { return ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_STEP; }
	@Override
	public SoundEvent getSwimSound() { return ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_SWIM; }
	@Override
	protected boolean canSpawnAsReinforcementInFluid() { return true; }
	@Override
	protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
		if (random.nextFloat() > 0.9) {
			if (random.nextInt(16) < 10) this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
			else this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
		}
	}
	@Override
	protected boolean prefersNewEquipment(ItemStack newStack, ItemStack currentStack, EquipmentSlot slot) {
		if (currentStack.isOf(Items.NAUTILUS_SHELL)) return false;
		return super.prefersNewEquipment(newStack, currentStack, slot);
	}
	@Override
	public boolean canSpawn(WorldView world) { return world.doesNotIntersectEntities(this); }
	public boolean canDrownedAttackTarget(LivingEntity target) {
		if (target != null) return !this.getEntityWorld().isDay() || target.isTouchingWater();
		return false;
	}
	@Override
	public boolean isPushedByFluids() { return !this.isSwimming(); }
	public boolean isTargetingUnderwater() {
		if (this.targetingUnderwater) return true;
		LivingEntity livingEntity = this.getTarget();
		return livingEntity != null && livingEntity.isTouchingWater();
	}
	@Override
	public void travel(Vec3d movementInput) {
		if (this.isSubmergedInWater() && this.isTargetingUnderwater()) {
			this.updateVelocity(0.01f, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
		}
		else super.travel(movementInput);
	}
	@Override
	public void updateSwimming() {
		if (!this.getEntityWorld().isClient()) this.setSwimming(this.canActVoluntarily() && this.isSubmergedInWater() && this.isTargetingUnderwater());
	}
	@Override
	public boolean isInSwimmingPose() { return this.isSwimming(); }
	protected boolean hasFinishedCurrentPath() {
		BlockPos blockPos;
		Path path = this.getNavigation().getCurrentPath();
		return path != null && (blockPos = path.getTarget()) != null && (this.squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ())) < 4.0;
	}
	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getMainHandStack();
		ItemStack itemStack2 = itemStack.isOf(Items.TRIDENT) ? itemStack : new ItemStack(Items.TRIDENT);
		TridentEntity tridentEntity = new TridentEntity(this.getEntityWorld(), this, itemStack2);
		double d = target.getX() - this.getX();
		double e = target.getBodyY(0.3333333333333333) - tridentEntity.getY();
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f);
		World world = this.getEntityWorld();
		if (world instanceof ServerWorld serverWorld) {
			ProjectileEntity.spawnWithVelocity(tridentEntity, serverWorld, itemStack2, d, e + g * 0.2, f, 1.6F, (float)(14 - world.getDifficulty().getId() * 4));
		}
		this.playSound(ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	}
	@Override
	public TagKey<Item> getPreferredWeapons() { return ItemTags.DROWNED_PREFERRED_WEAPONS; }
	public void setTargetingUnderwater(boolean targetingUnderwater) { this.targetingUnderwater = targetingUnderwater; }

	protected static class DrownedMoveControl extends MoveControl {
		private final DrownedVillagerEntity drowned;
		public DrownedMoveControl(DrownedVillagerEntity drowned) {
			super(drowned);
			this.drowned = drowned;
		}
		@Override
		public void tick() {
			LivingEntity livingEntity = this.drowned.getTarget();
			if (this.drowned.isTargetingUnderwater() && this.drowned.isTouchingWater()) {
				if (livingEntity != null && livingEntity.getY() > this.drowned.getY() || this.drowned.targetingUnderwater) {
					this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, 0.002, 0.0));
				}
				if (this.state != MoveControl.State.MOVE_TO || this.drowned.getNavigation().isIdle()) {
					this.drowned.setMovementSpeed(0.0f);
					return;
				}
				double d = this.targetX - this.drowned.getX();
				double e = this.targetY - this.drowned.getY();
				double f = this.targetZ - this.drowned.getZ();
				e /= Math.sqrt(d * d + e * e + f * f);
				this.drowned.setYaw(this.wrapDegrees(this.drowned.getYaw(), (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f, 90.0f));
				this.drowned.bodyYaw = this.drowned.getYaw();
				float i = (float)(this.speed * this.drowned.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
				float j = MathHelper.lerp(0.125f, this.drowned.getMovementSpeed(), i);
				this.drowned.setMovementSpeed(j);
				this.drowned.setVelocity(this.drowned.getVelocity().add(j * d * 0.005, j * e * 0.1, j * f * 0.005));
			}
			else {
				if (!this.drowned.isOnGround()) this.drowned.setVelocity(this.drowned.getVelocity().add(0, -0.008, 0));
				super.tick();
			}
		}
	}
	protected static class WanderAroundOnSurfaceGoal extends Goal {
		private final PathAwareEntity mob;
		private double x;
		private double y;
		private double z;
		private final double speed;
		private final World world;
		public WanderAroundOnSurfaceGoal(PathAwareEntity mob, double speed) {
			this.mob = mob;
			this.speed = speed;
			this.world = mob.getEntityWorld();
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}
		@Override
		public boolean canStart() {
			if (!this.world.isDay()) return false;
			if (this.mob.isTouchingWater()) return false;
			Vec3d vec3d = this.getWanderTarget();
			if (vec3d == null) return false;
			this.x = vec3d.x;
			this.y = vec3d.y;
			this.z = vec3d.z;
			return true;
		}
		@Override
		public boolean shouldContinue() { return !this.mob.getNavigation().isIdle(); }
		@Override
		public void start() { this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed); }
		private Vec3d getWanderTarget() {
			Random random = this.mob.getRandom();
			BlockPos blockPos = this.mob.getBlockPos();
			for (int i = 0; i < 10; ++i) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
				if (!this.world.getBlockState(blockPos2).isOf(Blocks.WATER)) continue;
				return Vec3d.ofBottomCenter(blockPos2);
			}
			return null;
		}
	}
	public static class TridentAttackGoal extends ProjectileAttackGoal {
		private final MobEntity mob;
		public TridentAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, float f) {
			super(rangedAttackMob, d, i, f);
			this.mob = (MobEntity)rangedAttackMob;
		}
		@Override
		public boolean canStart() { return super.canStart() && this.mob.getMainHandStack().isOf(Items.TRIDENT); }
		@Override
		public void start() {
			super.start();
			this.mob.setAttacking(true);
			this.mob.setCurrentHand(Hand.MAIN_HAND);
		}
		@Override
		public void stop() {
			super.stop();
			this.mob.clearActiveItem();
			this.mob.setAttacking(false);
		}
	}
	protected static class DrownedAttackGoal extends ZombieAttackGoal {
		private final DrownedVillagerEntity drowned;
		public DrownedAttackGoal(DrownedVillagerEntity drowned, double speed, boolean pauseWhenMobIdle) {
			super(drowned, speed, pauseWhenMobIdle);
			this.drowned = drowned;
		}
		@Override
		public boolean canStart() {
			return super.canStart() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
		}
		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.drowned.canDrownedAttackTarget(this.drowned.getTarget());
		}
	}
	protected static class LeaveWaterGoal extends MoveToTargetPosGoal {
		private final DrownedVillagerEntity drowned;
		public LeaveWaterGoal(DrownedVillagerEntity drowned, double speed) {
			super(drowned, speed, 8, 2);
			this.drowned = drowned;
		}
		@Override
		public boolean canStart() {
			return super.canStart() && !this.drowned.getEntityWorld().isDay() && this.drowned.isTouchingWater() && this.drowned.getY() >= (double)(this.drowned.getEntityWorld().getSeaLevel() - 3);
		}
		@Override
		protected boolean isTargetPos(WorldView world, BlockPos pos) {
			BlockPos blockPos = pos.up();
			if (!world.isAir(blockPos) || !world.isAir(blockPos.up())) return false;
			return world.getBlockState(pos).hasSolidTopSurface(world, pos, this.drowned);
		}
		@Override
		public void start() {
			this.drowned.setTargetingUnderwater(false);
			super.start();
		}
	}
	protected static class TargetAboveWaterGoal extends Goal {
		private final DrownedVillagerEntity drowned;
		private final double speed;
		private final int minY;
		private boolean foundTarget;
		public TargetAboveWaterGoal(DrownedVillagerEntity drowned, double speed, int minY) {
			this.drowned = drowned;
			this.speed = speed;
			this.minY = minY;
		}
		@Override
		public boolean canStart() {
			return !this.drowned.getEntityWorld().isDay() && this.drowned.isTouchingWater() && this.drowned.getY() < (double)(this.minY - 2);
		}
		@Override
		public boolean shouldContinue() { return this.canStart() && !this.foundTarget; }
		@Override
		public void tick() {
			if (this.drowned.getY() < (double)(this.minY - 1) && (this.drowned.getNavigation().isIdle() || this.drowned.hasFinishedCurrentPath())) {
				Vec3d vec3d = NoPenaltyTargeting.findTo(this.drowned, 4, 8, new Vec3d(this.drowned.getX(), this.minY - 1, this.drowned.getZ()), 1.5707963705062866);
				if (vec3d == null) {
					this.foundTarget = true;
					return;
				}
				this.drowned.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
			}
		}
		@Override
		public void start() {
			this.drowned.setTargetingUnderwater(true);
			this.foundTarget = false;
		}
		@Override
		public void stop() { this.drowned.setTargetingUnderwater(false); }
	}
}