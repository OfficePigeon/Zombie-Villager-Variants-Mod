package fun.wich.mixin;

import fun.wich.DrownedVillagerEntity;
import fun.wich.OfferGossipExposing;
import fun.wich.ZombieVillagerVariants;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin extends ZombieEntity implements OfferGossipExposing {
	@Shadow private @Nullable VillagerGossips gossip;
	@Shadow private @Nullable TradeOfferList offerData;
	@Shadow public abstract VillagerData getVillagerData();
	@Shadow public abstract int getExperience();
	public ZombieVillagerEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) { super(entityType, world); }
	@SuppressWarnings("AddedMixinMembersNamePattern")
	public VillagerGossips GetGossip() { return this.gossip; }
	@SuppressWarnings("AddedMixinMembersNamePattern")
	public TradeOfferList GetOfferData() { return this.offerData; }
	@Inject(method="canConvertInWater", at=@At("HEAD"), cancellable=true)
	private void AllowConversionInWater(CallbackInfoReturnable<Boolean> cir) { cir.setReturnValue(true); }
	@Override
	protected void convertInWater() {
		this.ConvertToDrownedVillagerEntity();
		if (!this.isSilent()) {
			this.getEntityWorld().syncWorldEvent(null, WorldEvents.ZOMBIE_CONVERTS_TO_DROWNED, this.getBlockPos(), 0);
		}
	}
	@Unique
	protected void ConvertToDrownedVillagerEntity() {
		this.convertTo((EntityType<? extends DrownedVillagerEntity>) ZombieVillagerVariants.DROWNED_VILLAGER, EntityConversionContext.create(this, true, true), zombieVillager -> {
			World world = getEntityWorld();
			if (world instanceof ServerWorld serverWorld) zombieVillager.initialize(serverWorld, getEntityWorld().getLocalDifficulty(zombieVillager.getBlockPos()), SpawnReason.CONVERSION, new ZombieData(false, true));
			zombieVillager.setVillagerData(this.getVillagerData());
			zombieVillager.setComponent(DataComponentTypes.VILLAGER_VARIANT, get(DataComponentTypes.VILLAGER_VARIANT));
			if (this.gossip != null) zombieVillager.setGossip(this.gossip.copy());
			if (this.offerData != null) zombieVillager.setOfferData(this.offerData.copy());
			zombieVillager.setExperience(this.getExperience());
			if (!this.isSilent()) world.syncWorldEvent(null, WorldEvents.ZOMBIE_INFECTS_VILLAGER, this.getBlockPos(), 0);
		});
	}
}
