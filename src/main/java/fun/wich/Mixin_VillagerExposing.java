package fun.wich;

import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerGossips;

public interface Mixin_VillagerExposing {
	VillagerData Mixin_VillagerExposing_GetVillagerData();
	VillagerGossips Mixin_VillagerExposing_GetGossip();
	TradeOfferList Mixin_VillagerExposing_GetOfferData();
	int Mixin_VillagerExposing_GetExperience();
}