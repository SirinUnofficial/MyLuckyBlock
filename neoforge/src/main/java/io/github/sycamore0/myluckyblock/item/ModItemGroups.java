package io.github.sycamore0.myluckyblock.item;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.component.ModDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItemGroups {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MYLUCKYBLOCK_GROUP;

    static {
        MYLUCKYBLOCK_GROUP = TABS.register("myluckyblock_group",
                () -> CreativeModeTab.builder()
                        .icon(() -> new ItemStack(ModBlocks.MY_LUCKY_BLOCK.get()))
                        .title(Component.translatable("itemGroup.myluckyblock.myluckyblock_group"))
                        .build());
    }

    public static void buildCreativeTabContent(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == MYLUCKYBLOCK_GROUP.get()) {
            event.accept(ModBlocks.MY_LUCKY_BLOCK.get());
            event.accept(luckyBlockWith(50));
            event.accept(luckyBlockWith(100));
            event.accept(luckyBlockWith(-50));
            event.accept(luckyBlockWith(-100));
        }
    }

    private static ItemStack luckyBlockWith(int luckyValue) {
        ItemStack itemStack = new ItemStack(ModBlocks.MY_LUCKY_BLOCK.get());
        itemStack.set(ModDataComponents.LUCKY_BLOCK_DATA, new LuckyBlockData(luckyValue, false, List.of()));
        return itemStack;
    }

    public static void onInitialize(IEventBus eventBus) {
        TABS.register(eventBus);
        eventBus.addListener(ModItemGroups::buildCreativeTabContent);
    }
}
