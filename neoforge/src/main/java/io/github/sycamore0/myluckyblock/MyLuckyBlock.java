package io.github.sycamore0.myluckyblock;

import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.config.ModConfig;
import io.github.sycamore0.myluckyblock.config.ModConfigManager;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import io.github.sycamore0.myluckyblock.screen.ConfigScreen;
import io.github.sycamore0.myluckyblock.worldgen.ModFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class MyLuckyBlock {
    public MyLuckyBlock(IEventBus eventBus, ModContainer container) {
        CommonClass.init();

        ModConfig config = ModConfigManager.loadConfig();
        container.registerExtensionPoint(IConfigScreenFactory.class, (MC, parent) -> new ConfigScreen(parent, config));

        ModBlocks.onInitialize(eventBus);
        ModItemGroups.onInitialize(eventBus);
        NeoForge.EVENT_BUS.register(ModEventHandlers.class);
        ModFeatures.onInitialize();
    }
}
