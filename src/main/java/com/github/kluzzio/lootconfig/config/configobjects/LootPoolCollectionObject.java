package com.github.kluzzio.lootconfig.config.configobjects;

import java.util.List;

public class LootPoolCollectionObject {
    private final List<String> LootPools;

    public LootPoolCollectionObject(List<String> lootpools) {
        LootPools = lootpools;
    }

    public List<String> getLootPools() {
        return LootPools;
    }
}