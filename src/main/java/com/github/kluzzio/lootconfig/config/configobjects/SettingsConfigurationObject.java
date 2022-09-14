package com.github.kluzzio.lootconfig.config.configobjects;

import java.util.List;
import java.util.Map;

public class SettingsConfigurationObject {

    public boolean LoadPoolsAtRuntime;
    public Map<String, List<String>> NameDefinitions;
    public Map<String, LootPoolObject> LootPoolDefinitions;

    public SettingsConfigurationObject(boolean loadPoolsAtRuntime, Map<String, List<String>> nameDefinitions, Map<String, LootPoolObject> lootDefinitions) {
        this.LoadPoolsAtRuntime = loadPoolsAtRuntime;
        this.NameDefinitions = nameDefinitions;
        this.LootPoolDefinitions = lootDefinitions;
    }

    public boolean getloadPoolsAtRuntime() {
        return LoadPoolsAtRuntime;
    }

    public Map<String, List<String>> getNameDefinitions() {
        return NameDefinitions;
    }

    public Map<String, LootPoolObject> getLootPoolDefinitions() {
        return LootPoolDefinitions;
    }
}
