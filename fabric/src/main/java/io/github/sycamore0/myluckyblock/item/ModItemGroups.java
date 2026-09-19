package io.github.sycamore0.myluckyblock.item;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.component.ModDataComponents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ModItemGroups {
    public static final CreativeModeTab MYLUCKYBLOCK_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.MY_LUCKY_BLOCK))
            .title(Component.translatableEscape("itemGroup.myluckyblock.myluckyblock_group"))
            .displayItems((context, entries) -> {
                entries.accept(ModBlocks.MY_LUCKY_BLOCK);
                entries.accept(luckyBlockWith(50));
                entries.accept(luckyBlockWith(100));
                entries.accept(luckyBlockWith(-50));
                entries.accept(luckyBlockWith(-100));
            })
            .build();

    public static void addBlockToGroup(ItemStack itemStack) {
        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "myluckyblock_group"))).register(entries -> {
            entries.accept(itemStack);
        });
    }

    private static ItemStack luckyBlockWith(int luckyValue) {
        ItemStack itemStack = new ItemStack(ModBlocks.MY_LUCKY_BLOCK);
        itemStack.set(ModDataComponents.LUCKY_BLOCK_DATA, new LuckyBlockData(luckyValue, false, List.of()));
        return itemStack;
    }

    public static void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "myluckyblock_group"), MYLUCKYBLOCK_GROUP);
    }
}
