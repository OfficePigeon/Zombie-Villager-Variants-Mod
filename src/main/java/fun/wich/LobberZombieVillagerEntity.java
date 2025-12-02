package fun.wich;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class LobberZombieVillagerEntity extends ExtendedZombieVillagerEntity implements RangedAttackMob {
	protected static final int HARD_ATTACK_INTERVAL = 20;
	protected static final int REGULAR_ATTACK_INTERVAL = 40;
	protected ThrowerRangedAttackGoal rangedAttackGoal;
	protected static final TrackedData<Boolean> IS_ATTACKING_RANGED = DataTracker.registerData(LobberZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public LobberZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) { super(entityType, world); }
	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(2, new ThrowerAttackGoal(this, 1.0, false, 16));
		this.goalSelector.add(2, rangedAttackGoal = new ThrowerRangedAttackGoal(this, 1.25, HARD_ATTACK_INTERVAL, HARD_ATTACK_INTERVAL + 20, 10, 16));
		this.initSharedZombieVillagerGoals();
	}
	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		double d = target.getX() - this.getX();
		double e = target.getEyeY() - 1.1;
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f) * 0.2;
		if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
			ProjectileEntity entity = new LobberZombieVillagerThrownFleshEntity(serverWorld, this);
			entity.setVelocity(d, e + g - entity.getY(), f, 1.6F, 6.0F);
			serverWorld.spawnEntity(entity);
		}
		this.playSound(ZombieVillagerVariants.ENTITY_LOBBER_ZOMBIE_VILLAGER_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	}
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData);
		this.updateAttackType();
		return entityData;
	}
	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(IS_ATTACKING_RANGED, false);
	}
	public boolean IsAttackingRanged() { return this.dataTracker.get(IS_ATTACKING_RANGED); }
	public void SetAttackingRanged(boolean value) { this.dataTracker.set(IS_ATTACKING_RANGED, value); }
	public void updateAttackType() {
		if (this.getEntityWorld() != null && !this.getEntityWorld().isClient()) {
			int i = this.getEntityWorld().getDifficulty() != Difficulty.HARD ? REGULAR_ATTACK_INTERVAL : HARD_ATTACK_INTERVAL;
			if (this.rangedAttackGoal != null) this.rangedAttackGoal.setAttackInterval(i, i + 20);
		}
	}
	@Override public Arm getMainArm() { return Arm.LEFT; }
	@Override
	protected void initEquipment(Random random, LocalDifficulty difficulty) {
		super.initEquipment(random, difficulty);
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.ROTTEN_FLESH));
		this.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
	}
	@Override
	public void readCustomDataFromNbt(NbtCompound view) {
		super.readCustomDataFromNbt(view);
		this.updateAttackType();
	}
	@Override
	public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
		super.onEquipStack(slot, oldStack, newStack);
		this.updateAttackType();
	}
	@Override
	public SoundEvent GetCureSound() { return ZombieVillagerVariants.ENTITY_LOBBER_ZOMBIE_VILLAGER_CURE; }
	@Override
	public SoundEvent getAmbientSound() { return ZombieVillagerVariants.ENTITY_LOBBER_ZOMBIE_VILLAGER_AMBIENT; }
	@Override
	public SoundEvent getHurtSound(DamageSource source) { return ZombieVillagerVariants.ENTITY_LOBBER_ZOMBIE_VILLAGER_HURT; }
	@Override
	public SoundEvent getDeathSound() { return ZombieVillagerVariants.ENTITY_LOBBER_ZOMBIE_VILLAGER_DEATH; }
	@Override
	public SoundEvent getStepSound() { return ZombieVillagerVariants.ENTITY_LOBBER_ZOMBIE_VILLAGER_STEP; }

	protected static class ThrowerAttackGoal extends MeleeAttackGoal {
		public final LobberZombieVillagerEntity entity;
		public final float range;
		public int ticks;
		public ThrowerAttackGoal(LobberZombieVillagerEntity entity, double speed, boolean pauseWhenMobIdle, float range) {
			super(entity, speed, pauseWhenMobIdle);
			this.entity = entity;
			this.range = range;
		}
		@Override
		public void start() {
			super.start();
			this.ticks = 0;
		}
		@Override
		public void stop() {
			super.stop();
			this.entity.setAttacking(false);
		}
		@Override
		public void tick() {
			super.tick();
			++this.ticks;
			this.entity.setAttacking(this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2);
		}
		@Override public boolean canStart() { return super.canStart() && this.closeEnough(); }
		@Override public boolean shouldContinue() { return super.shouldContinue() && this.closeEnough(); }
		public boolean closeEnough() {
			return entity.getTarget() != null && entity.squaredDistanceTo(entity.getTarget()) < this.range;
		}
	}
	protected static class ThrowerRangedAttackGoal extends Goal {
		private final LobberZombieVillagerEntity entity;
		private LivingEntity target;
		private int updateCountdownTicks;
		private final double mobSpeed;
		private int seenTargetTicks;
		protected int minIntervalTicks;
		protected int maxIntervalTicks;
		private final float maxShootRange;
		public final float range;
		private final float squaredMaxShootRange;

		public ThrowerRangedAttackGoal(LobberZombieVillagerEntity entity, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange, float range) {
			this.updateCountdownTicks = -1;
			this.entity = entity;
			this.mobSpeed = mobSpeed;
			this.minIntervalTicks = minIntervalTicks;
			this.maxIntervalTicks = maxIntervalTicks;
			this.maxShootRange = maxShootRange;
			this.range = range;
			this.squaredMaxShootRange = maxShootRange * maxShootRange;
			this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		}
		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.entity.getTarget();
			if (livingEntity != null && livingEntity.isAlive()) {
				this.target = livingEntity;
				return farEnough();
			}
			return false;
		}
		@Override
		public boolean shouldContinue() {
			return this.canStart() || (this.target != null && this.target.isAlive()) && !this.entity.getNavigation().isIdle() && this.farEnough();
		}
		@Override
		public void stop() {
			this.target = null;
			this.seenTargetTicks = 0;
			this.updateCountdownTicks = -1;
			this.entity.SetAttackingRanged(false);
		}
		public boolean farEnough() { return entity.getTarget() == null || !(entity.squaredDistanceTo(entity.getTarget()) < this.range); }
		@Override
		public boolean shouldRunEveryTick() { return true; }
		public void setAttackInterval(int minIntervalTicks, int maxIntervalTicks) {
			this.minIntervalTicks = minIntervalTicks;
			this.maxIntervalTicks = maxIntervalTicks;
		}
		@Override
		public void tick() {
			if (this.target == null) return;
			double d = this.entity.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
			boolean bl = this.entity.getVisibilityCache().canSee(this.target);
			if (bl) ++this.seenTargetTicks;
			else this.seenTargetTicks = 0;
			if (!(d > (double)this.squaredMaxShootRange) && this.seenTargetTicks >= 5) this.entity.getNavigation().stop();
			else this.entity.getNavigation().startMovingTo(this.target, this.mobSpeed);
			this.entity.getLookControl().lookAt(this.target, 30.0F, 30.0F);
			if (--this.updateCountdownTicks == 0) {
				if (!bl) return;
				float f = (float)Math.sqrt(d) / this.maxShootRange;
				float g = MathHelper.clamp(f, 0.1F, 1.0F);
				this.entity.shootAt(this.target, g);
				this.updateCountdownTicks = MathHelper.floor(f * (float)(this.maxIntervalTicks - this.minIntervalTicks) + (float)this.minIntervalTicks);
			}
			else if (this.updateCountdownTicks < 0) {
				this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(d) / this.maxShootRange, this.minIntervalTicks, this.maxIntervalTicks));
			}
			this.entity.SetAttackingRanged(true);
		}
	}
}