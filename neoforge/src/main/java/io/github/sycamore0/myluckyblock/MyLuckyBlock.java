package io.github.sycamore0.myluckyblock;

import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class MyLuckyBlock {
    public MyLuckyBlock(IEventBus eventBus) {
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        ModBlocks.registerModBlocks(eventBus);
        ModItemGroups.registerModItemGroups(eventBus);
        NeoForge.EVENT_BUS.register(ModEventHandlers.class);

        CommonClass.addModId(Constants.MOD_ID);
    }
}
