package com.github.levoment.chestlootmodifier.api;

import com.github.levoment.chestlootmodifier.ConfigManager;
import com.github.levoment.chestlootmodifier.ConfigurationObject;
import com.github.levoment.chestlootmodifier.LootPoolCollectionObject;
import com.github.levoment.chestlootmodifier.LootPoolObject;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class LootTableEventHelper {

    public static Collection<LootPool> modifyLootPoolsFromConfig(ConfigurationObject configurationObject, Identifier id) {
        Collection<LootPool> lootPools = new java.util.ArrayList<>(List.of());
        if (configurationObject.getLootTableIds().containsKey(id.toString())) {
            LootPoolCollectionObject lootPoolCollection = configurationObject.getLootTableIds().get(id.toString());
            Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.SETTINGS_CONFIG.getLootPoolDefinitions();
            for (String lootPool : lootPoolCollection.getLootPools()) {
                if (lootPoolDefinitions.containsKey(lootPool)) {
                    lootPools.add(makeLootPoolFromConfig(lootPool, lootPoolDefinitions));
                }
            }
        }
        return lootPools;
    }

    public static Collection<LootPool> replaceLootPoolsFromConfig(ConfigurationObject configurationObject, Identifier id, LootTable original) {
        Collection<LootPool> lootPools = new java.util.ArrayList<>(List.of(original.pools));
        if (configurationObject.getLootTableIds().containsKey(id.toString())) {
            lootPools = new java.util.ArrayList<>(List.of());
            LootPoolCollectionObject lootPoolCollection = configurationObject.getLootTableIds().get(id.toString());
            Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.SETTINGS_CONFIG.getLootPoolDefinitions();
            for (String lootPool : lootPoolCollection.getLootPools()) {
                if (lootPoolDefinitions.containsKey(lootPool)) {
                    lootPools.add(makeLootPoolFromConfig(lootPool, lootPoolDefinitions));
                }
            }
        }
        return lootPools;
    }

    private static LootPool makeLootPoolFromConfig(String lootPool, Map<String, LootPoolObject> lootPoolDefinitions) {
        LootPoolObject currentLootPool = lootPoolDefinitions.get(lootPool);
        LootPool.Builder lootPoolBuilder = LootPool.builder();
        setRolls(lootPoolBuilder, currentLootPool);
        setRollChance(lootPoolBuilder, currentLootPool);
        setBonusRolls(lootPoolBuilder, currentLootPool);
        // TODO setConditions(lootPoolBuilder, currentLootPool);
        setEntries(lootPoolBuilder, currentLootPool);
        return lootPoolBuilder.build();
    }

    private static void setRolls(LootPool.Builder lootPoolBuilder, LootPoolObject currentLootPool) {
        int minRolls = currentLootPool.getMinRolls();
        int maxRolls = currentLootPool.getMaxRolls();
        lootPoolBuilder.rolls(UniformLootNumberProvider.create(minRolls, maxRolls));
    }

    private static void setRollChance(LootPool.Builder lootPoolBuilder, LootPoolObject currentLootPool) {
        float rollChanceSuccess = currentLootPool.getRollSuccessChance();
        if (rollChanceSuccess < 1.0f) {
            float lootingRollSuccessModifier = currentLootPool.getLootingRollSuccessModifier();
            lootPoolBuilder.conditionally(RandomChanceWithLootingLootCondition.builder(
                    rollChanceSuccess, lootingRollSuccessModifier
            ));
        }
    }

    private static void setBonusRolls(LootPool.Builder lootPoolBuilder, LootPoolObject currentLootPool) {
        float minBonusRolls = currentLootPool.getMinBonusRolls();
        float maxBonusRolls = currentLootPool.getMaxBonusRolls();
        lootPoolBuilder.bonusRolls(UniformLootNumberProvider.create(minBonusRolls, maxBonusRolls));
    }

    private static void setConditions(LootPool.Builder lootPoolBuilder, LootPoolObject currentLootPool) {
        // TODO Enact Conditions
    }

    private static void setEntries(LootPool.Builder lootPoolBuilder, LootPoolObject currentLootPool) {
        Map<String, List<Integer>> entries = currentLootPool.getEntries();
        entries.forEach((entry, entryInformation) -> LootPoolInterpreter.addItem(entry, lootPoolBuilder, entryInformation));
    }
}
