package io.github.sycamore0.myluckyblock.item;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final CreativeModeTab MYLUCKYBLOCK_GROUP = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.MY_LUCKY_BLOCK))
            .title(Component.translatableEscape("itemGroup.myluckyblock.myluckyblock_group"))
            .displayItems((context, entries) -> entries.accept(ModBlocks.MY_LUCKY_BLOCK))
            .build();

    public static void addBlockToGroup(ItemStack itemStack) {
        CreativeModeTabEvents.modifyOutputEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "myluckyblock_group"))).register(entries -> entries.accept(itemStack));
    }

    public static void onInitialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "myluckyblock_group"), MYLUCKYBLOCK_GROUP);
    }
}
