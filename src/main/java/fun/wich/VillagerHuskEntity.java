package fun.wich;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class VillagerHuskEntity extends ZombieVillagerEntity {
	public VillagerHuskEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) { super(entityType, world); }
	public static boolean canSpawn(EntityType<VillagerHuskEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return canSpawnInDark(type, world, spawnReason, pos, random) && (SpawnReason.isAnySpawner(spawnReason) || world.isSkyVisible(pos));
	}
	@Override
	public void handleStatus(byte status) {
		if (status == 16) {
			if (!this.isSilent()) {
				this.getEntityWorld().playSoundClient(this.getX(), this.getEyeY(), this.getZ(), ZombieVillagerVariants.ENTITY_VILLAGER_HUSK_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
			}
		}
		else super.handleStatus(status);
	}
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
	public boolean tryAttack(ServerWorld world, Entity target) {
		boolean bl = super.tryAttack(world, target);
		if (bl && this.getMainHandStack().isEmpty() && target instanceof LivingEntity) {
			float f = this.getEntityWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
			((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 140 * (int)f), this);
		}
		return bl;
	}
	@Override
	protected boolean canConvertInWater() { return true; }
	@Override
	protected void convertInWater() {
		this.ConvertToZombieVillagerEntity();
		if (!this.isSilent()) this.getEntityWorld().syncWorldEvent(null, WorldEvents.HUSK_CONVERTS_TO_ZOMBIE, this.getBlockPos(), 0);
	}
	protected void ConvertToZombieVillagerEntity() {
		this.convertTo((EntityType<? extends ZombieVillagerEntity>) EntityType.ZOMBIE_VILLAGER, EntityConversionContext.create(this, true, true), zombieVillager -> {
			World world = getEntityWorld();
			if (world instanceof ServerWorld serverWorld) zombieVillager.initialize(serverWorld, getEntityWorld().getLocalDifficulty(zombieVillager.getBlockPos()), SpawnReason.CONVERSION, new ZombieData(false, true));
			zombieVillager.setVillagerData(getVillagerData());
			zombieVillager.setComponent(DataComponentTypes.VILLAGER_VARIANT, get(DataComponentTypes.VILLAGER_VARIANT));
			VillagerGossips gossip = ((OfferGossipExposing)this).GetGossip();
			if (gossip != null) zombieVillager.setGossip(gossip.copy());
			TradeOfferList offers = ((OfferGossipExposing)this).GetOfferData();
			if (offers != null) zombieVillager.setOfferData(offers.copy());
			zombieVillager.setExperience(getExperience());
			zombieVillager.setVillagerData(getVillagerData());
			if (!this.isSilent()) world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
		});
	}
	@Override
	public boolean infectVillager(ServerWorld world, VillagerEntity villager) {
		ZombieVillagerEntity zombieVillagerEntity = villager.convertTo(ZombieVillagerVariants.VILLAGER_HUSK, EntityConversionContext.create(villager, true, true), zombieVillager -> {
			zombieVillager.initialize(world, world.getLocalDifficulty(zombieVillager.getBlockPos()), SpawnReason.CONVERSION, new ZombieData(false, true));
			zombieVillager.setVillagerData(villager.getVillagerData());
			zombieVillager.setGossip(villager.getGossip().copy());
			zombieVillager.setOfferData(villager.getOffers().copy());
			zombieVillager.setExperience(villager.getExperience());
			if (!this.isSilent()) world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
		});
		return zombieVillagerEntity != null;
	}
}
