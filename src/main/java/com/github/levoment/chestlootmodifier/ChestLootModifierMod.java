package com.github.levoment.chestlootmodifier;

import com.github.levoment.chestlootmodifier.api.LootTableEventHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChestLootModifierMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("chestlootmodifier");
    public static final String MOD_NAME_LOG_ID = "[Chest Loot Modifier Mod]";

    @Override
    public void onInitialize() {
        // Create the config file if it doesn't exist
        ConfigManager.createConfigFile();
        // Read the config file if it exists
        ConfigManager.readConfigFile();

        // If the configuration was loaded successfully
        if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
            // Return if LoadPoolsAtRuntime is false
            //if (ConfigManager.CURRENT_CONFIG.loadPoolsAtRuntime()) return;

            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
                    LootTableEventHelper.addLootPools(ConfigManager.CURRENT_CONFIG, id, tableBuilder));
        }
    }
}
