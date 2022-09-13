package com.github.levoment.chestlootmodifier.mixins;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootableContainerBlockEntity.class)
public interface LootableContainerBlockEntityAccessor {

    @Accessor("lootTableId")
    Identifier getLootTableId();
}
