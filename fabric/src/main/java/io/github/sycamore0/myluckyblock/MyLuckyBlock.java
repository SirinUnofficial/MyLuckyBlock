package io.github.sycamore0.myluckyblock;

import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import io.github.sycamore0.myluckyblock.worldgen.ModFeatures;
import net.fabricmc.api.ModInitializer;

public class MyLuckyBlock implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();
        ModBlocks.onInitialize();
        ModItemGroups.onInitialize();
        ModEventHandlers.onInitialize();
        ModFeatures.onInitialize();
    }
}