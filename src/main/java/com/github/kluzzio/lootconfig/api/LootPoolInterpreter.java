package com.github.kluzzio.lootconfig.api;

import com.github.kluzzio.lootconfig.LootConfig;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class LootPoolInterpreter {

    public static void addItem(String entry, LootPool.Builder lootPoolBuilder, List<Integer> entryInformation) {
        if (entry.contains("(") && entry.contains(")")) {
            String potionID = entry.substring(entry.indexOf("(") + 1, entry.indexOf(")"));
            entry = entry.substring(0, entry.contains(" ") ? entry.indexOf(" ") : entry.indexOf("("));
            String entryCopy = entry;
            Item item = Registry.ITEM.getOrEmpty(new Identifier(entry)).orElseThrow(() -> {
                throw new JsonSyntaxException(LootConfig.MOD_NAME_LOG_ID + " Unknown item '" + new Identifier(entryCopy) + "'");
            });
            if (item == Items.POTION || item == Items.TIPPED_ARROW) {
                Potion potion = Registry.POTION.getOrEmpty(new Identifier(potionID)).orElseThrow(() -> {
                    throw new JsonSyntaxException(LootConfig.MOD_NAME_LOG_ID + " Unknown potion '" + new Identifier(potionID) + "'");
                });
                if (potion == Potions.EMPTY)
                    LootConfig.LOGGER.warn(LootConfig.MOD_NAME_LOG_ID + " " + potionID + " was not found as a valid identifier for an potion.");
                addPotionAffectedItemToPool(lootPoolBuilder, item, entryInformation, potion);
                return;
            }
            LootConfig.LOGGER.warn(LootConfig.MOD_NAME_LOG_ID + " " + entry + " may have been marked with a potion effect in a lootpool.");
        }
        String entryCopy = entry;
        Item item = Registry.ITEM.getOrEmpty(new Identifier(entry)).orElseThrow(() -> {
            throw new JsonSyntaxException(LootConfig.MOD_NAME_LOG_ID + " Unknown item '" + new Identifier(entryCopy) + "'");
        });
        if (item == Items.AIR)
            LootConfig.LOGGER.warn(LootConfig.MOD_NAME_LOG_ID + " " + entry + " was not found as a valid identifier for an item.");
        else lootPoolBuilder.with(ItemEntry.builder(item).weight(entryInformation.get(1))
                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(
                        entryInformation.get(0)))));
    }

    public static void addPotionAffectedItemToPool(LootPool.Builder lootPoolBuilder, Item entryItem, List<Integer> entryInformation, Potion potion) {
        lootPoolBuilder.with(ItemEntry.builder(entryItem).weight(entryInformation.get(1))
                .apply(SetPotionLootFunction.builder(potion))
                .apply(SetCountLootFunction.builder(
                        ConstantLootNumberProvider.create(entryInformation.get(0))))
        );
    }
}