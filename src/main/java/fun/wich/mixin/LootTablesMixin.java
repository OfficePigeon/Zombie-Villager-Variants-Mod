package fun.wich.mixin;

import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootTables.class)
public interface LootTablesMixin {
	@Invoker("registerLootTable") static RegistryKey<LootTable> registerLootTable(RegistryKey<LootTable> key) { return null; }
}