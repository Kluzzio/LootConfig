package com.github.levoment.chestlootmodifier;

import java.util.Map;

public class ConfigurationObject {

    public Map<String, LootPoolCollectionObject> LootTableIds;
    public Map<String, LootPoolCollectionObject> Names;
    public Map<String, LootPoolObject> LootPoolDefinitions;

    public ConfigurationObject(Map<String, LootPoolCollectionObject> lootTableIds, Map<String, LootPoolObject> lootPoolDefinitions) {
        this.LootTableIds = lootTableIds;
        this.LootPoolDefinitions = lootPoolDefinitions;
    }

    public Map<String, LootPoolCollectionObject> getLootTableIds() {
        return LootTableIds;
    }

    public void setLootTableIds(Map<String, LootPoolCollectionObject> lootTableIds) {
       this.LootTableIds = lootTableIds;
    }

    public Map<String, LootPoolCollectionObject> getNames() {
        return Names;
    }

    public void setNames(Map<String, LootPoolCollectionObject> names) {
        this.Names = names;
    }

    public Map<String, LootPoolObject> getLootPoolDefinitions() {
        return LootPoolDefinitions;
    }

    public void setLootPoolDefinitions(Map<String, LootPoolObject> lootPoolDefinitions) {
        LootPoolDefinitions = lootPoolDefinitions;
    }

    public boolean loadPoolsAtRuntime() {
        return false;
    }
}
