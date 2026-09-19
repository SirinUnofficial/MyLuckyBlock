package io.github.sycamore0.myluckyblock;

import io.github.sycamore0.myluckyblock.block.ModBlockEntities;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.command.ModCommands;
import io.github.sycamore0.myluckyblock.component.ModDataComponents;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import io.github.sycamore0.myluckyblock.pack.ModPacks;
import io.github.sycamore0.myluckyblock.worldgen.ModFeatures;
import net.fabricmc.api.ModInitializer;

public class MyLuckyBlock implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();
        ModDataComponents.onInitialize();
        ModBlocks.onInitialize();
        ModBlockEntities.onInitialize();
        ModItemGroups.onInitialize();
        ModEventHandlers.onInitialize();
        ModFeatures.onInitialize();
        ModPacks.onInitialize();
        ModCommands.onInitialize();
    }
}