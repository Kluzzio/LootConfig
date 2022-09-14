package com.github.kluzzio.lootconfig.api;

import com.github.kluzzio.lootconfig.LootConfig;
import com.github.kluzzio.lootconfig.config.ConfigManager;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.loot.LootTable;

import java.util.HashMap;

public class VanillaLootPoolHelper {

    public static final HashMap<String, LootTable> VANILLA_LOOT_TABLES = new HashMap<>();

    public static void init() {
        ConfigManager.interpretConfigFile(LootConfig.REPLACE_CONFIG_FILE);
        if (ConfigManager.SUCCESSFULLY_LOADED_REPLACE) {
            LootTableEvents.REPLACE.register(((resourceManager, lootManager, id, original, source) -> {
                if (source == LootTableSource.VANILLA) {
                    if (ConfigManager.REPLACE_CONFIG.getLootTableIds().containsKey(id.toString())) {
                        if (ConfigManager.REPLACE_CONFIG.getLootTableIds().get(id.toString()).getLootPools().contains("Vanilla")) {
                            addLootPoolToList(id.toString(), original);
                        }
                    }
                    for (String key : ConfigManager.REPLACE_CONFIG.getNames().keySet()) {
                        if (key.contains(id.toString())) {
                            if (ConfigManager.REPLACE_CONFIG.getNames().get(key).getLootPools().contains("Vanilla")) {
                                addLootPoolToList(id.toString(), original);
                            }
                        }
                    }
                }
                return original;
            }));
        }
    }

    private static void addLootPoolToList(String key, LootTable lootTable) {
        VANILLA_LOOT_TABLES.put(key, lootTable);
    }
}
