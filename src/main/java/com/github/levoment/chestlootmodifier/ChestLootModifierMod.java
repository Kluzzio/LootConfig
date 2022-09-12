package com.github.levoment.chestlootmodifier;

import com.github.levoment.chestlootmodifier.api.LootTableEventHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ChestLootModifierMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("chestlootmodifier");
    public static final String MOD_NAME_LOG_ID = "[Chest Loot Modifier Mod]";

    public static final String SETTINGS_CONFIG_FILE = "loot_config/settings_and_definitions_config.json";
    public static final String MODIFY_CONFIG_FILE =  "loot_config/modify_config.json";
    public static final String REPLACE_CONFIG_FILE =  "loot_config/replace_config.json";

    @Override
    public void onInitialize() {
        ConfigManager.interpretConfigFile(SETTINGS_CONFIG_FILE);
        if (ConfigManager.SUCCESSFULLY_LOADED_SETTINGS) {
            if (ConfigManager.SETTINGS_CONFIG.getloadPoolsAtRuntime())
                return; //Run at mixin instead if loadPoolsAtRuntime is true. Default == false

            ConfigManager.interpretConfigFile(MODIFY_CONFIG_FILE);
            if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
                LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
                        tableBuilder.pools(LootTableEventHelper.modifyLootPoolsFromConfig(ConfigManager.CURRENT_CONFIG, id)));
            }
            ConfigManager.interpretConfigFile(REPLACE_CONFIG_FILE);
            if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
                LootTableEvents.REPLACE.register(((resourceManager, lootManager, id, original, source) ->
                        LootTable.builder().pools(LootTableEventHelper.replaceLootPoolsFromConfig(ConfigManager.CURRENT_CONFIG, id, original)).build()));
            }
        }
    }
}
