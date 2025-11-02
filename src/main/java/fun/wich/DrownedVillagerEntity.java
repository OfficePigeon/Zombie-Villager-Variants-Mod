package fun.wich;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class DrownedVillagerEntity extends DrownedEntity implements VillagerDataContainer {
	private static final TrackedData<Boolean> CONVERTING = DataTracker.registerData(DrownedVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(DrownedVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
	private static final int BASE_CONVERSION_DELAY = 3600;
	private static final int DEFAULT_CONVERSION_TIME = -1;
	private static final int DEFAULT_EXPERIENCE = 0;
	private int conversionTimer;
	@Nullable
	private UUID converter;
	@Nullable
	private VillagerGossips gossip;
	@Nullable
	private TradeOfferList offerData;
	private int experience = DEFAULT_EXPERIENCE;

	public DrownedVillagerEntity(EntityType<? extends DrownedEntity> entityType, World world) { super(entityType, world); }

	public static boolean canDrownedVillagerSpawn(EntityType<DrownedVillagerEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
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

	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(CONVERTING, false);
		builder.add(VILLAGER_DATA, this.createVillagerData());
	}
	protected void writeCustomData(WriteView view) {
		super.writeCustomData(view);
		view.put("VillagerData", VillagerData.CODEC, this.getVillagerData());
		view.putNullable("Offers", TradeOfferList.CODEC, this.offerData);
		view.putNullable("Gossips", VillagerGossips.CODEC, this.gossip);
		view.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : DEFAULT_CONVERSION_TIME);
		view.putNullable("ConversionPlayer", Uuids.INT_STREAM_CODEC, this.converter);
		view.putInt("Xp", this.experience);
	}
	protected void readCustomData(ReadView view) {
		super.readCustomData(view);
		this.dataTracker.set(VILLAGER_DATA, view.read("VillagerData", VillagerData.CODEC).orElseGet(this::createVillagerData));
		this.offerData = view.read("Offers", TradeOfferList.CODEC).orElse(null);
		this.gossip = view.read("Gossips", VillagerGossips.CODEC).orElse(null);
		int i = view.getInt("ConversionTime", DEFAULT_CONVERSION_TIME);
		if (i != DEFAULT_CONVERSION_TIME) {
			UUID uUID = view.read("ConversionPlayer", Uuids.INT_STREAM_CODEC).orElse(null);
			this.setConverting(uUID, i);
		}
		else {
			this.getDataTracker().set(CONVERTING, false);
			this.conversionTimer = DEFAULT_CONVERSION_TIME;
		}
		this.experience = view.getInt("Xp", DEFAULT_EXPERIENCE);
	}
	private VillagerData createVillagerData() {
		World world = this.getEntityWorld();
		Optional<RegistryEntry.Reference<VillagerProfession>> optional = Registries.VILLAGER_PROFESSION.getRandom(this.random);
		VillagerData villagerData = VillagerEntity.createVillagerData().withType(world.getRegistryManager(), VillagerType.forBiome(world.getBiome(this.getBlockPos())));
		if (optional.isPresent()) villagerData = villagerData.withProfession(optional.get());
		return villagerData;
	}
	public void tick() {
		if (!this.getEntityWorld().isClient() && this.isAlive() && this.isConverting()) {
			int i = this.getConversionRate();
			this.conversionTimer -= i;
			if (this.conversionTimer <= 0) this.finishConversion((ServerWorld)this.getEntityWorld());
		}
		super.tick();
	}
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.GOLDEN_APPLE)) {
			if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
				itemStack.decrementUnlessCreative(1, player);
				if (!this.getEntityWorld().isClient()) this.setConverting(player.getUuid(), this.random.nextInt(2401) + BASE_CONVERSION_DELAY);
				return ActionResult.SUCCESS_SERVER;
			}
			else return ActionResult.CONSUME;
		}
		else return super.interactMob(player, hand);
	}
	protected boolean canConvertInWater() { return false; } //TODO: Allow standard zombie villagers to convert in water
	public boolean canImmediatelyDespawn(double distanceSquared) { return !this.isConverting() && this.experience == DEFAULT_EXPERIENCE; }
	public boolean isConverting() { return this.getDataTracker().get(CONVERTING); }
	private void setConverting(@Nullable UUID uuid, int delay) {
		this.converter = uuid;
		this.conversionTimer = delay;
		this.getDataTracker().set(CONVERTING, true);
		this.removeStatusEffect(StatusEffects.WEAKNESS);
		this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getEntityWorld().getDifficulty().getId() - 1, 0)));
		this.getEntityWorld().sendEntityStatus(this, (byte)16);
	}
	public void handleStatus(byte status) {
		if (status == 16) {
			if (!this.isSilent()) {
				this.getEntityWorld().playSoundClient(this.getX(), this.getEyeY(), this.getZ(), ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
			}
		}
		else super.handleStatus(status);
	}
	private void finishConversion(ServerWorld world) {
		this.convertTo(EntityType.VILLAGER, EntityConversionContext.create(this, false, false), (villager) -> {
			for (EquipmentSlot equipmentSlot : this.dropForeignEquipment(world, (stack) -> !EnchantmentHelper.hasAnyEnchantmentsWith(stack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE))) {
				StackReference stackReference = villager.getStackReference(equipmentSlot.getEntitySlotId() + 300);
				stackReference.set(this.getEquippedStack(equipmentSlot));
			}
			villager.setVillagerData(this.getVillagerData());
			if (this.gossip != null) villager.readGossipData(this.gossip);
			if (this.offerData != null) villager.setOffers(this.offerData.copy());
			villager.setExperience(this.experience);
			villager.initialize(world, world.getLocalDifficulty(villager.getBlockPos()), SpawnReason.CONVERSION, null);
			villager.reinitializeBrain(world);
			if (this.converter != null) {
				PlayerEntity playerEntity = world.getPlayerByUuid(this.converter);
				if (playerEntity instanceof ServerPlayerEntity) {
					Criteria.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)playerEntity, this, villager);
					world.handleInteraction(EntityInteraction.ZOMBIE_VILLAGER_CURED, playerEntity, villager);
				}
			}
			villager.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
			if (!this.isSilent()) world.syncWorldEvent(null, 1027, this.getBlockPos(), 0);
		});
	}
	public void setConversionTimer(int conversionTimer) { this.conversionTimer = conversionTimer; }
	private int getConversionRate() {
		int i = 1;
		if (this.random.nextFloat() < 0.01F) {
			int j = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
				for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
					for(int m = (int)this.getZ() - 4; m < (int)this.getZ() + 4 && j < 14; ++m) {
						BlockState blockState = this.getEntityWorld().getBlockState(mutable.set(k, l, m));
						if (blockState.isOf(Blocks.IRON_BARS) || blockState.getBlock() instanceof BedBlock) {
							if (this.random.nextFloat() < 0.3F) ++i;
							++j;
						}
					}
				}
			}
		}
		return i;
	}
	public float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}
	public SoundEvent getAmbientSound() {
		return this.isTouchingWater() ? ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_AMBIENT_WATER : ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_AMBIENT;
	}
	public SoundEvent getHurtSound(DamageSource source) {
		return this.isTouchingWater() ? ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_HURT_WATER : ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_HURT;
	}
	public SoundEvent getDeathSound() {
		return this.isTouchingWater() ? ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_DEATH_WATER : ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_DEATH;
	}
	public SoundEvent getStepSound() { return ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_STEP; }
	public SoundEvent getSwimSound() { return ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_SWIM; }
	public void setOfferData(TradeOfferList offerData) { this.offerData = offerData; }
	public void setGossip(VillagerGossips gossip) { this.gossip = gossip; }

	public void shootAt(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getMainHandStack();
		ItemStack itemStack2 = itemStack.isOf(Items.TRIDENT) ? itemStack : new ItemStack(Items.TRIDENT);
		TridentEntity tridentEntity = new TridentEntity(this.getEntityWorld(), this, itemStack2);
		double d = target.getX() - this.getX();
		double e = target.getBodyY(0.3333333333333333) - tridentEntity.getY();
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f);
		World var15 = this.getEntityWorld();
		if (var15 instanceof ServerWorld serverWorld) {
			ProjectileEntity.spawnWithVelocity(tridentEntity, serverWorld, itemStack2, d, e + g * 0.2, f, 1.6F, (float)(14 - this.getEntityWorld().getDifficulty().getId() * 4));
		}
		this.playSound(ZombieVillagerVariants.ENTITY_DROWNED_VILLAGER_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	public void setVillagerData(VillagerData villagerData) {
		VillagerData villagerData2 = this.getVillagerData();
		if (!villagerData2.profession().equals(villagerData.profession())) {
			this.offerData = null;
		}

		this.dataTracker.set(VILLAGER_DATA, villagerData);
	}
	public VillagerData getVillagerData() { return this.dataTracker.get(VILLAGER_DATA); }
	public int getExperience() { return this.experience; }
	public void setExperience(int experience) { this.experience = experience; }
	@Nullable
	public <T> T get(ComponentType<? extends T> type) {
		return type == DataComponentTypes.VILLAGER_VARIANT ? castComponentValue(type, this.getVillagerData().type()) : super.get(type);
	}
	protected void copyComponentsFrom(ComponentsAccess from) {
		this.copyComponentFrom(from, DataComponentTypes.VILLAGER_VARIANT);
		super.copyComponentsFrom(from);
	}
	protected <T> boolean setApplicableComponent(ComponentType<T> type, T value) {
		if (type == DataComponentTypes.VILLAGER_VARIANT) {
			RegistryEntry<VillagerType> registryEntry = castComponentValue(DataComponentTypes.VILLAGER_VARIANT, value);
			this.setVillagerData(this.getVillagerData().withType(registryEntry));
			return true;
		}
		else return super.setApplicableComponent(type, value);
	}

	@Override
	public boolean infectVillager(ServerWorld world, VillagerEntity villager) {
		DrownedVillagerEntity zombieVillagerEntity = villager.convertTo(ZombieVillagerVariants.DROWNED_VILLAGER, EntityConversionContext.create(villager, true, true), zombieVillager -> {
			zombieVillager.initialize(world, world.getLocalDifficulty(zombieVillager.getBlockPos()), SpawnReason.CONVERSION, new ZombieData(false, true));
			zombieVillager.setVillagerData(villager.getVillagerData());
			zombieVillager.setGossip(villager.getGossip().copy());
			zombieVillager.setOfferData(villager.getOffers().copy());
			zombieVillager.setExperience(villager.getExperience());
			if (!this.isSilent()) {
				world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
			}
		});
		return zombieVillagerEntity != null;
	}
}
