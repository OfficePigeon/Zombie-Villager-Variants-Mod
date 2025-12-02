package fun.wich;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class BoulderingZombieVillagerEntity extends ExtendedZombieVillagerEntity {
	private static final TrackedData<Boolean> CLIMBING = DataTracker.registerData(BoulderingZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public BoulderingZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) { super(entityType, world); }
	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(CLIMBING, false);
	}
	@Override protected EntityNavigation createNavigation(World world) { return new SpiderNavigation(this, world); }
	@Override
	public void tick() {
		super.tick();
		if (!this.getEntityWorld().isClient()) this.setClimbingWall(this.horizontalCollision);
	}
	@Override public boolean isClimbing() { return this.isClimbingWall(); }
	public boolean isClimbingWall() { return this.dataTracker.get(CLIMBING); }
	public void setClimbingWall(boolean climbing) { this.dataTracker.set(CLIMBING, climbing); }
	@Override
	public SoundEvent GetCureSound() { return ZombieVillagerVariants.ENTITY_BOULDERING_ZOMBIE_VILLAGER_CURE; }
	@Override
	public boolean burnsInDaylight() { return false; }
	@Override
	public SoundEvent getAmbientSound() { return ZombieVillagerVariants.ENTITY_BOULDERING_ZOMBIE_VILLAGER_AMBIENT; }
	@Override
	public SoundEvent getHurtSound(DamageSource source) { return ZombieVillagerVariants.ENTITY_BOULDERING_ZOMBIE_VILLAGER_HURT; }
	@Override
	public SoundEvent getDeathSound() { return ZombieVillagerVariants.ENTITY_BOULDERING_ZOMBIE_VILLAGER_DEATH; }
	@Override
	public SoundEvent getStepSound() {
		return isClimbing() ? ZombieVillagerVariants.ENTITY_BOULDERING_ZOMBIE_VILLAGER_CLIMB : ZombieVillagerVariants.ENTITY_BOULDERING_ZOMBIE_VILLAGER_STEP;
	}
}