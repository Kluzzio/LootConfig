package com.github.kluzzio.lootconfig.mixins;

import com.github.kluzzio.lootconfig.LootConfig;
import com.github.kluzzio.lootconfig.api.LootTableEventHelper;
import com.github.kluzzio.lootconfig.config.ConfigManager;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Inject(method = "getTable", at = @At("RETURN"), cancellable = true)
    public void breakShitProbably(Identifier id, CallbackInfoReturnable<LootTable> cir) {

        ConfigManager.interpretConfigFile(LootConfig.SETTINGS_CONFIG_FILE);
        if (ConfigManager.SUCCESSFULLY_LOADED_SETTINGS) {
            if (!ConfigManager.SETTINGS_CONFIG.getloadPoolsAtRuntime())
                return; //Run at init instead if loadPoolsAtRuntime is false. Default == false

            LootTable lootTable = cir.getReturnValue();
            Collection<LootPool> lootPoolCollection = new java.util.ArrayList<>(List.of());

            ConfigManager.interpretConfigFile(LootConfig.REPLACE_CONFIG_FILE);
            if (ConfigManager.SUCCESSFULLY_LOADED_REPLACE) {
                lootTable = LootTable.builder().pools(LootTableEventHelper.replaceLootPoolsFromConfig(ConfigManager.REPLACE_CONFIG, id, lootTable)).build();
            }

            ConfigManager.interpretConfigFile(LootConfig.MODIFY_CONFIG_FILE);
            if (ConfigManager.SUCCESSFULLY_LOADED_MODIFY) {
                lootPoolCollection.addAll(LootTableEventHelper.modifyLootPoolsFromConfig(ConfigManager.MODIFY_CONFIG, id));
            }

            cir.setReturnValue(FabricLootTableBuilder.copyOf(lootTable).pools(lootPoolCollection).build());
        }
    }
}
