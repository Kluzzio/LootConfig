package com.github.levoment.chestlootmodifier;

import com.github.levoment.chestlootmodifier.api.LootPoolInterpreter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // Create the config file if it doesn't exist
            ConfigManager.createConfigFile();
            // Read the config file
            ConfigManager.readConfigFile();

            // If the configuration was loaded successfully
            if (ConfigManager.SUCCESSFULLY_LOADED_CONFIG) {
                // Return if LoadPoolsAtRuntime is false
                //if (ConfigManager.CURRENT_CONFIG.loadPoolsAtRuntime()) return;

                ConfigManager.CURRENT_CONFIG.getLootTableIds().forEach((key, lootPoolCollection) -> {

                    if (key.equals(id.toString())) {
                        Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.CURRENT_CONFIG.getLootPoolDefinitions();
                        for (String lootPool : lootPoolCollection.getLootPools()) {
                            if (lootPoolDefinitions.containsKey(lootPool)) {
                                LootPoolObject currentLootPool = lootPoolDefinitions.get(lootPool);
                                LootPool.Builder lootPoolBuilder = LootPool.builder();
                                int minRolls = currentLootPool.getMinRolls();
                                int maxRolls = currentLootPool.getMaxRolls();
                                lootPoolBuilder.rolls(UniformLootNumberProvider.create(minRolls, maxRolls));
                                float rollChanceSuccess = currentLootPool.getRollSuccessChance();
                                if (rollChanceSuccess < 1.0f) {
                                    float lootingRollSuccessModifier = currentLootPool.getLootingRollSuccessModifier();
                                    lootPoolBuilder.conditionally(RandomChanceWithLootingLootCondition.builder(
                                            rollChanceSuccess, lootingRollSuccessModifier
                                    ));
                                }
                                float minBonusRolls = currentLootPool.getMinBonusRolls();
                                float maxBonusRolls = currentLootPool.getMaxBonusRolls();
                                lootPoolBuilder.bonusRolls(UniformLootNumberProvider.create(minBonusRolls, maxBonusRolls));
                                // TODO Enact Conditions
                                Map<String, List<Integer>> entries = currentLootPool.getEntries();
                                entries.forEach((entry, entryInformation) -> LootPoolInterpreter.addItem(entry, lootPoolBuilder, entryInformation));
                                tableBuilder.pool(lootPoolBuilder.build());
                            }
                        }
                    }
                });
            }
        });
    }
}
