package com.github.kluzzio.lootconfig.api;

import com.github.kluzzio.lootconfig.LootConfig;
import com.github.kluzzio.lootconfig.config.ConfigManager;
import com.github.kluzzio.lootconfig.config.configobjects.ConfigurationObject;
import com.github.kluzzio.lootconfig.config.configobjects.LootPoolCollectionObject;
import com.github.kluzzio.lootconfig.config.configobjects.LootPoolObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.*;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

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
        for (String key : configurationObject.getNames().keySet()) {
            if (ConfigManager.SETTINGS_CONFIG.getNameDefinitions().containsKey(key)) {
                if (ConfigManager.SETTINGS_CONFIG.getNameDefinitions().get(key).contains(id.toString())) {
                    Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.SETTINGS_CONFIG.getLootPoolDefinitions();
                    for (String lootPool : configurationObject.getNames().get(key).getLootPools()) {
                        if (lootPoolDefinitions.containsKey(lootPool)) {
                            lootPools.add(makeLootPoolFromConfig(lootPool, lootPoolDefinitions));
                        }
                    }
                }
            }
        }
        return lootPools;
    }

    public static Collection<LootPool> replaceLootPoolsFromConfig(ConfigurationObject configurationObject, Identifier id, LootTable original) {
        Collection<LootPool> lootPools = new java.util.ArrayList<>(List.of(original.pools));
        boolean bl = false;
        if (configurationObject.getLootTableIds().containsKey(id.toString())) {
            lootPools = new java.util.ArrayList<>(List.of()); bl = true;
            Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.SETTINGS_CONFIG.getLootPoolDefinitions();
            for (String lootPool : configurationObject.getLootTableIds().get(id.toString()).getLootPools()) {
                if (lootPoolDefinitions.containsKey(lootPool)) {
                    lootPools.add(makeLootPoolFromConfig(lootPool, lootPoolDefinitions));
                }
            }
        }
        for (String key : configurationObject.getNames().keySet()) {
            if (ConfigManager.SETTINGS_CONFIG.getNameDefinitions().containsKey(key)) {
                if (ConfigManager.SETTINGS_CONFIG.getNameDefinitions().get(key).contains(id.toString())) {
                    Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.SETTINGS_CONFIG.getLootPoolDefinitions();
                    if (!bl)
                        lootPools = new java.util.ArrayList<>(List.of());
                    for (String lootPool : configurationObject.getNames().get(key).getLootPools()) {
                        if (lootPoolDefinitions.containsKey(lootPool)) {
                            lootPools.add(makeLootPoolFromConfig(lootPool, lootPoolDefinitions));
                        }
                    }
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
        setConditions(lootPoolBuilder, currentLootPool);
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
        Map<String, Map<String, List<Integer>>> conditions = currentLootPool.getConditions();

        if (conditions.containsKey("KilledByPlayer"))
            lootPoolBuilder.conditionally(KilledByPlayerLootCondition.builder());
        if (conditions.containsKey("LocationCheck_Biome")) {
            Map<String, List<Integer>> conditionSet = conditions.get("LocationCheck_Biome");
            for (String conInfo : conditionSet.keySet()) {
                lootPoolBuilder.conditionally(LocationCheckLootCondition.builder(LocationPredicate.Builder.create()
                        .biome(RegistryKey.of(Registry.BIOME_KEY, new Identifier(conInfo)))));
            }
        }
        if (conditions.containsKey("MatchTool_Enchantment")) {
            Map<String, List<Integer>> matchToolCondition = conditions.get("MatchTool_Enchantment");
            for (String conInfo : matchToolCondition.keySet()) {
                Enchantment enchantment = Registry.ENCHANTMENT.getOrEmpty(new Identifier(conInfo)).orElseThrow(() -> {
                    throw new JsonSyntaxException(LootConfig.MOD_NAME_LOG_ID + " Unknown enchantment '" + new Identifier(conInfo) + "'");
                });
                lootPoolBuilder.conditionally(MatchToolLootCondition.builder(ItemPredicate.Builder.create()
                        .enchantment(new EnchantmentPredicate(enchantment,
                                NumberRange.IntRange.between(matchToolCondition.get(conInfo).get(0),
                                        matchToolCondition.get(conInfo).get(1))))));
            }
        }
        if (conditions.containsKey("SurvivesExplosion"))
            lootPoolBuilder.conditionally(SurvivesExplosionLootCondition.builder());
        if (conditions.containsKey("WeatherCheck")) {
            Map<String, List<Integer>> weatherCondition = conditions.get("WeatherCheck");
            if (weatherCondition.containsKey("RainingTrue"))
                lootPoolBuilder.conditionally(WeatherCheckLootCondition.create().raining(true));
            else if (weatherCondition.containsKey("RainingFalse"))
                lootPoolBuilder.conditionally(WeatherCheckLootCondition.create().raining(false));
            if (weatherCondition.containsKey("ThunderingTrue"))
                lootPoolBuilder.conditionally(WeatherCheckLootCondition.create().thundering(true));
            else if (weatherCondition.containsKey("ThunderingFalse"))
                lootPoolBuilder.conditionally(WeatherCheckLootCondition.create().thundering(false));
            if (weatherCondition.containsKey("RainingOrThundering"))
                lootPoolBuilder.conditionally(WeatherCheckLootCondition.create().raining(true).or(WeatherCheckLootCondition.create().thundering(true)));
        }
    }

    private static void setEntries(LootPool.Builder lootPoolBuilder, LootPoolObject currentLootPool) {
        Map<String, List<Integer>> entries = currentLootPool.getEntries();
        entries.forEach((entry, entryInformation) -> LootPoolInterpreter.addItem(entry, lootPoolBuilder, entryInformation));
    }
}
