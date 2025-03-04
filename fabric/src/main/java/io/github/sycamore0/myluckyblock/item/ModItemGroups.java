package io.github.sycamore0.myluckyblock.item;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
    public static final CreativeModeTab MYLUCKYBLOCK_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.MY_LUCKY_BLOCK))
            .title(Component.translatableEscape("itemGroup.myluckyblock.myluckyblock_group"))
            .displayItems((context, entries) -> {
                entries.accept(ModBlocks.MY_LUCKY_BLOCK);
            })
            .build();

    public static void registerModItemGroups() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(MyLuckyBlock.MOD_ID, "myluckyblock_group"), MYLUCKYBLOCK_GROUP);
    }

}
