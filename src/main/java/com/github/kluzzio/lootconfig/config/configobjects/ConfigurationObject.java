package com.github.kluzzio.lootconfig.config.configobjects;

import java.util.Map;

public class ConfigurationObject {

    public Map<String, LootPoolCollectionObject> LootTableIds;
    public Map<String, LootPoolCollectionObject> Names;

    public ConfigurationObject(Map<String, LootPoolCollectionObject> lootTableIds, Map<String, LootPoolCollectionObject> names) {
        this.LootTableIds = lootTableIds;
        this.Names = names;
    }

    public Map<String, LootPoolCollectionObject> getLootTableIds() {
        return LootTableIds;
    }

    public Map<String, LootPoolCollectionObject> getNames() {
        return Names;
    }
}
