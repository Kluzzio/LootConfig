package com.github.kluzzio.lootconfig.config.configobjects;

import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
public class LootPoolObject {
    private int MinRolls;
    private int MaxRolls;
    private float RollSuccessChance;
    private float LootingRollSuccessModifier;
    private float MinBonusRolls;
    private float MaxBonusRolls;
    private Map<String, Map<String, List<Integer>>> Conditions;
    private Map<String, List<Integer>> Entries;

    public LootPoolObject(int minRolls, int maxRolls, float rollSuccessChance, float lootingRollSuccessModifier,
                          float minBonusRolls, float maxBonusRolls, Map<String, Map<String, List<Integer>>> conditions, Map<String, List<Integer>> entries) {
        MinRolls = minRolls;
        MaxRolls = maxRolls;
        RollSuccessChance = rollSuccessChance;
        LootingRollSuccessModifier = lootingRollSuccessModifier;
        MinBonusRolls = minBonusRolls;
        MaxBonusRolls = maxBonusRolls;
        Conditions = conditions;
        Entries = entries;
    }

    public int getMinRolls() {
        return MinRolls;
    }

    public int getMaxRolls() {
        return MaxRolls;
    }

    public float getRollSuccessChance() {
        return RollSuccessChance;
    }

    public float getLootingRollSuccessModifier() {
        return LootingRollSuccessModifier;
    }

    public float getMinBonusRolls() {
        return MinBonusRolls;
    }

    public float getMaxBonusRolls() {
        return MaxBonusRolls;
    }

    public Map<String, Map<String, List<Integer>>> getConditions() {
        return Conditions;
    }

    public Map<String, List<Integer>> getEntries() {
        return Entries;
    }
}
