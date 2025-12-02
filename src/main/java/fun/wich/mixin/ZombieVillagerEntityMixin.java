package fun.wich.mixin;

import fun.wich.ExtendedZombieVillagerEntity;
import fun.wich.Mixin_VillagerExposing;
import fun.wich.ZombieVillagerFreezeTracker;
import fun.wich.ZombieVillagerVariants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin extends ZombieEntity implements Mixin_VillagerExposing, ZombieVillagerFreezeTracker {
	@Unique @SuppressWarnings("WrongEntityDataParameterClass")
	private static final TrackedData<Boolean> ZOMBIE_VILLAGER_CONVERTING_IN_SNOW = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);;
	public ZombieVillagerEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) { super(entityType, world); }
	//Expose Villager Fields
	@Shadow private VillagerGossips gossip;
	@Shadow private TradeOfferList offerData;
	@Shadow public abstract VillagerData getVillagerData();
	@Shadow public abstract int getExperience();
	@Override
	public VillagerData Mixin_VillagerExposing_GetVillagerData() { return this.getVillagerData(); }
	@Override
	public VillagerGossips Mixin_VillagerExposing_GetGossip() { return this.gossip; }
	@Override
	public TradeOfferList Mixin_VillagerExposing_GetOfferData() { return this.offerData; }
	@Override
	public int Mixin_VillagerExposing_GetExperience() { return this.getExperience(); }
	//Conversion in Water
	@Inject(method="canConvertInWater", at=@At("HEAD"), cancellable=true)
	private void AllowConversionInWater(CallbackInfoReturnable<Boolean> cir) {
		if (getType() == EntityType.ZOMBIE_VILLAGER) cir.setReturnValue(true);
	}
	@Override
	protected void convertInWater() {
		ExtendedZombieVillagerEntity.ConvertToZombieVillagerEntity((ZombieVillagerEntity)(Object)this, ZombieVillagerVariants.DROWNED_VILLAGER, ZombieVillagerVariants.ENTITY_ZOMBIE_VILLAGER_CONVERTED_TO_DROWNED_VILLAGER);
	}
	//Conversion in Powder Snow
	@Unique
	private int inPowderSnowTime;
	@Unique
	private int ticksUntilSnowConversion;
	@Inject(method="initDataTracker", at=@At("TAIL"))
	protected void Mixin_InitDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
		builder.add(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW, false);
	}
	@Override
	public boolean canFreeze() {
		if (this.getType() == EntityType.ZOMBIE_VILLAGER) {
			if (ZombieVillagerVariants.FrozenZombiesDefined()) return false; //don't freeze if frozen zombies exist
		}
		return super.canFreeze();
	}
	@Override
	public boolean ZombieVillagerFreezeTracker_IsShaking() { return this.getDataTracker().get(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW); }
	@Inject(method="writeCustomData", at=@At("TAIL"))
	protected void Mixin_WriteCustomData(WriteView view, CallbackInfo ci) {
		view.putInt("VillagerInPowderSnow", this.isTouchingWater() ? this.inPowderSnowTime : -1);
		view.putInt("VillagerSnowConversionTime", this.getDataTracker().get(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW) ? this.ticksUntilSnowConversion : -1);
	}
	@Inject(method="readCustomData", at=@At("TAIL"))
	protected void Mixin_ReadCustomData(ReadView view, CallbackInfo ci) {
		this.inPowderSnowTime = view.getInt("VillagerInPowderSnow", -1);
		int i = view.getInt("VillagerSnowConversionTime", -1);
		if (i < 0) this.ticksUntilSnowConversion = -1;
		this.getDataTracker().set(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW, false);
	}
	@Inject(method="tick", at=@At("HEAD"))
	public void Mixin_Tick(CallbackInfo ci) {
		if (this.getType() != EntityType.ZOMBIE_VILLAGER) return; //only default zombie villagers can convert
		if (!ZombieVillagerVariants.FrozenZombiesDefined()) return; //can only convert in powdered snow if frozen zombies exist
		World world = this.getEntityWorld();
		if (!world.isClient() && this.isAlive() && !this.isAiDisabled()) {
			if (this.inPowderSnow || this.wasInPowderSnow) {
				if (this.getDataTracker().get(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW)) {
					--this.ticksUntilSnowConversion;
					if (this.ticksUntilSnowConversion < 0) {
						this.convertTo(ZombieVillagerVariants.FROZEN_ZOMBIE_VILLAGER, EntityConversionContext.create(this, true, true), zombie -> {
							if (!this.isSilent()) {
								this.getEntityWorld().playSound(null, this.getBlockPos(), ZombieVillagerVariants.ENTITY_ZOMBIE_VILLAGER_CONVERTED_TO_FROZEN_ZOMBIE_VILLAGER, SoundCategory.HOSTILE, 2.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1);
							}
						});
					}
				}
				else {
					++this.inPowderSnowTime;
					if (this.inPowderSnowTime >= 600) {
						this.ticksUntilSnowConversion = 300;
						this.getDataTracker().set(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW, true);
					}
				}
			}
			else {
				this.inPowderSnowTime = -1;
				this.getDataTracker().set(ZOMBIE_VILLAGER_CONVERTING_IN_SNOW, false);
			}
		}
	}
}