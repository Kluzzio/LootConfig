package com.github.levoment.chestlootmodifier.mixins;

import com.github.levoment.chestlootmodifier.ChestLootModifierMod;
import com.github.levoment.chestlootmodifier.config.ConfigManager;
import com.github.levoment.chestlootmodifier.config.configobjects.LootPoolObject;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceWithLootingLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    private LootTable chestLootModifierModModifiedLootTable;
    private boolean addPool;

    @Shadow
    Identifier lootTableId;

    @Inject(method = "checkLootInteraction(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void checkLootInteractionMixinCallback(PlayerEntity player, CallbackInfo ci) {
        this.addPool = false;
        if (this.lootTableId != null && ((LootableContainerBlockEntity) ((Object) this)).getWorld().getServer() != null) {
            MinecraftServer minecraftServer = ((LootableContainerBlockEntity) ((Object) this)).getWorld().getServer();
            // Create the config file if it doesn't exist
            ConfigManager.createConfigFile(ChestLootModifierMod.MODIFY_CONFIG_FILE);
            // Read the config file
            ConfigManager.readConfigFile(ChestLootModifierMod.MODIFY_CONFIG_FILE);

            // If the configuration was loaded successfully
            if (ConfigManager.SUCCESSFULLY_LOADED_SETTINGS) {
                // Return if LoadPoolsAtRuntime is false
                if (!ConfigManager.SETTINGS_CONFIG.getloadPoolsAtRuntime()) return;
                ConfigManager.MODIFY_CONFIG.getLootTableIds().forEach((key, lootPoolCollection) -> {

                    if (key.equals(lootTableId.toString())) {
                        Map<String, LootPoolObject> lootPoolDefinitions = ConfigManager.SETTINGS_CONFIG.getLootPoolDefinitions();
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
                                entries.forEach((entry, entryInformation) -> {
                                    Item entryItem = Registry.ITEM.get(new Identifier(entry));
                                    lootPoolBuilder.with(ItemEntry.builder(entryItem).weight(entryInformation.get(1))
                                            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(
                                                    entryInformation.get(0))
                                            )));
                                });
                                // Add the item to the pool of items for the current chest
                                LootManager lootManager = minecraftServer.getLootManager();
                                LootTable lootTable = lootManager.getTable(this.lootTableId);
                                this.chestLootModifierModModifiedLootTable = FabricLootTableBuilder.copyOf(lootTable).pool(lootPoolBuilder.build()).build();

                            }
                        }
                    }
                });
            }
        }
    }

    @ModifyVariable(method = "checkLootInteraction(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "STORE", id = "lootTable"))
    public LootTable modifyLootTable(LootTable lootTable) {
        // If the configuration was loaded successfully
        if (ConfigManager.SUCCESSFULLY_LOADED_SETTINGS) {
            // Return if LoadPoolsAtRuntime is false
            //if (!ConfigManager.CURRENT_CONFIG.loadPoolsAtRuntime()) return lootTable;
        }
        if (this.addPool) {
            return this.chestLootModifierModModifiedLootTable;
        } else {
            return lootTable;
        }
    }
}