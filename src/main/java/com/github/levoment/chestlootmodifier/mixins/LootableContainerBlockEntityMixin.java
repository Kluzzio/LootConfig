package com.github.levoment.chestlootmodifier.mixins;

import com.github.levoment.chestlootmodifier.ChestLootModifierMod;
import com.github.levoment.chestlootmodifier.api.LootTableEventHelper;
import com.github.levoment.chestlootmodifier.config.ConfigManager;
import net.fabricmc.fabric.api.loot.v2.FabricLootTableBuilder;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    private LootTable chestLootModifierModModifiedLootTable;

    //TODO Mixin for entities / non chests
    @Inject(method = "checkLootInteraction(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void checkLootInteractionMixinCallback(PlayerEntity player, CallbackInfo ci) {
        if ((Object) this instanceof LootableContainerBlockEntity containerBlock) {
            Identifier lootTableId = ((LootableContainerBlockEntityAccessor) containerBlock).getLootTableId();
            if (lootTableId != null && containerBlock.getWorld().getServer() != null) { //lootTableId is null after the chest has been populated
                MinecraftServer minecraftServer = containerBlock.getWorld().getServer();

                ConfigManager.interpretConfigFile(ChestLootModifierMod.SETTINGS_CONFIG_FILE);
                if (ConfigManager.SUCCESSFULLY_LOADED_SETTINGS) {
                    if (!ConfigManager.SETTINGS_CONFIG.getloadPoolsAtRuntime())
                        return; //Run at init instead if loadPoolsAtRuntime is false. Default == false

                    LootManager lootManager = minecraftServer.getLootManager();
                    LootTable lootTable = lootManager.getTable(lootTableId);
                    Collection<LootPool> lootPoolCollection = new java.util.ArrayList<>(List.of());

                    ConfigManager.interpretConfigFile(ChestLootModifierMod.REPLACE_CONFIG_FILE);
                    if (ConfigManager.SUCCESSFULLY_LOADED_REPLACE) {
                        lootTable = LootTable.builder().pools(LootTableEventHelper.replaceLootPoolsFromConfig(ConfigManager.REPLACE_CONFIG, lootTableId, lootTable)).build();
                    }

                    ConfigManager.interpretConfigFile(ChestLootModifierMod.MODIFY_CONFIG_FILE);
                    if (ConfigManager.SUCCESSFULLY_LOADED_MODIFY) {
                        lootPoolCollection.addAll(LootTableEventHelper.modifyLootPoolsFromConfig(ConfigManager.MODIFY_CONFIG, lootTableId));
                    }

                    this.chestLootModifierModModifiedLootTable =
                            FabricLootTableBuilder.copyOf(lootTable).pools(lootPoolCollection).build();
                }
            }
        }
    }

    @ModifyVariable(method = "checkLootInteraction(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "STORE", id = "lootTable"))
    public LootTable modifyLootTable(LootTable lootTable) {
        if (ConfigManager.SUCCESSFULLY_LOADED_SETTINGS) {
            // Return if LoadPoolsAtRuntime is false
            if (!ConfigManager.SETTINGS_CONFIG.getloadPoolsAtRuntime()) return lootTable;
        }
        return this.chestLootModifierModModifiedLootTable;
    }
}