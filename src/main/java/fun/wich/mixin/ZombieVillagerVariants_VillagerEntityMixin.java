package fun.wich.mixin;

import fun.wich.Mixin_VillagerExposing;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VillagerEntity.class)
public abstract class ZombieVillagerVariants_VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer, Mixin_VillagerExposing {
	@Shadow public abstract VillagerGossips getGossip();
	@Shadow public abstract int getExperience();
	@Shadow public abstract VillagerData getVillagerData();
	public ZombieVillagerVariants_VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) { super(entityType, world); }
	@Override
	public VillagerData Mixin_VillagerExposing_GetVillagerData() { return this.getVillagerData(); }
	@Override
	public VillagerGossips Mixin_VillagerExposing_GetGossip() { return this.getGossip(); }
	@Override
	public TradeOfferList Mixin_VillagerExposing_GetOfferData() { return this.getOffers(); }
	@Override
	public int Mixin_VillagerExposing_GetExperience() { return this.getExperience(); }
}