package com.github.levoment.chestlootmodifier;

import java.util.List;
import java.util.Map;

public class LootPoolObject {
    private int MinRolls;
    private int MaxRolls;
    private float RollSuccessChance;
    private float LootingRollSuccessModifier;
    private float MinBonusRolls;
    private float MaxBonusRolls;
    private List<String> Conditions;
    private Map<String, List<Integer>> Entries;

    public LootPoolObject(int minRolls, int maxRolls, float rollSuccessChance, float lootingRollSuccessModifier,
                          float minBonusRolls, float maxBonusRolls, List<String> conditions, Map<String, List<Integer>> entries) {
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

    public List<String> getConditions() {
        return Conditions;
    }

    public Map<String, List<Integer>> getEntries() {
        return Entries;
    }
}
