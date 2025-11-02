package fun.wich;

import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerGossips;

public interface OfferGossipExposing {
	VillagerGossips GetGossip();
	TradeOfferList GetOfferData();
}
