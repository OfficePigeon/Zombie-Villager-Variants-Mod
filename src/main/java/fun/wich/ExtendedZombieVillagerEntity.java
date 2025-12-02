package fun.wich;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public abstract class ExtendedZombieVillagerEntity extends ZombieVillagerEntity {
	public ExtendedZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> entityType, World world) {
		super(entityType, world);
	}
	protected void initSharedZombieVillagerGoals() {
		this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, (new RevengeGoal(this)).setGroupRevenge(ZombifiedPiglinEntity.class));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, false));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new ActiveTargetGoal<>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}
	public abstract SoundEvent GetCureSound();
	@Override
	public void handleStatus(byte status) {
		if (status == 16) {
			if (!this.isSilent()) {
				this.getEntityWorld().playSound(this.getX(), this.getEyeY(), this.getZ(), GetCureSound(), this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
			}
		}
		else super.handleStatus(status);
	}
	public RegistryEntry<StatusEffect> GetStatusEffectOnHit() { return null; }
	@Override
	public boolean tryAttack(Entity target) {
		boolean bl = super.tryAttack(target);
		if (bl) {
			RegistryEntry<StatusEffect> statusEffect = GetStatusEffectOnHit();
			if (statusEffect != null) {
				if (this.getMainHandStack().isEmpty() && target instanceof LivingEntity) {
					float f = this.getEntityWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
					((LivingEntity)target).addStatusEffect(new StatusEffectInstance(statusEffect, 140 * (int)f), this);
				}
			}
		}
		return bl;
	}
	public static void ProcessZombieVillagerConversion(World world, MobEntity villager, ZombieVillagerEntity zombieVillager, SoundEvent conversionSound) {
		BlockPos pos = zombieVillager.getBlockPos();
		if (world instanceof ServerWorld serverWorld) zombieVillager.initialize(serverWorld, world.getLocalDifficulty(pos), SpawnReason.CONVERSION, new ZombieData(false, true));
		if (villager instanceof VillagerEntity villagerEntity) {
			VillagerData data = villagerEntity.getVillagerData();
			if (data != null) zombieVillager.setVillagerData(data);
			VillagerGossips gossip = villagerEntity.getGossip();
			if (gossip != null) zombieVillager.setGossipData(gossip.serialize(NbtOps.INSTANCE));
			TradeOfferList offers = villagerEntity.getOffers();
			if (offers != null) zombieVillager.setOfferData(offers.copy());
			zombieVillager.setXp(villagerEntity.getExperience());
		}
		else if (villager instanceof Mixin_VillagerExposing mixinVillager) {
			VillagerData data = mixinVillager.Mixin_VillagerExposing_GetVillagerData();
			if (data != null) zombieVillager.setVillagerData(data);
			VillagerGossips gossip = mixinVillager.Mixin_VillagerExposing_GetGossip();
			if (gossip != null) zombieVillager.setGossipData(gossip.serialize(NbtOps.INSTANCE));
			TradeOfferList offers = mixinVillager.Mixin_VillagerExposing_GetOfferData();
			if (offers != null) zombieVillager.setOfferData(offers.copy());
			zombieVillager.setXp(mixinVillager.Mixin_VillagerExposing_GetExperience());
		}
		else if (villager instanceof ZombieVillagerEntity zombieVillagerEntity) {
			VillagerData data = zombieVillagerEntity.getVillagerData();
			if (data != null) zombieVillager.setVillagerData(data);
		}
		Random random = villager.getRandom();
		if (!zombieVillager.isSilent() && conversionSound != null) {
			world.playSoundAtBlockCenter(pos, conversionSound, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1, false);
		}
	}
	public static void ConvertToZombieVillagerEntity(ZombieVillagerEntity source, EntityType<? extends ZombieVillagerEntity> type, SoundEvent conversionSound) {
		ZombieVillagerEntity zombieVillager = source.convertTo(type, true);
		if (zombieVillager != null) ProcessZombieVillagerConversion(source.getEntityWorld(), source, zombieVillager, conversionSound);
	}
	@Override
	public boolean onKilledOther(ServerWorld world, LivingEntity other) {
		if ((world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity villager) {
			if (world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) return false;
			//noinspection unchecked
			return InfectVillager(world, villager, (EntityType<? extends ZombieVillagerEntity>)getType());
		}
		return true;
	}
	public static boolean InfectVillager(ServerWorld world, VillagerEntity villager, EntityType<? extends ZombieVillagerEntity> type) {
		ZombieVillagerEntity zombieVillager = villager.convertTo(type, true);
		if (zombieVillager != null) ProcessZombieVillagerConversion(world, villager, zombieVillager, SoundEvents.ENTITY_ZOMBIE_INFECT);
		return zombieVillager != null;
	}
}