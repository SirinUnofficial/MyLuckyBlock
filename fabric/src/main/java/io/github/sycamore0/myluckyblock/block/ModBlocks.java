package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    public static final Block MY_LUCKY_BLOCK;
    public static final Block DEBUG_LUCKY_BLOCK;

    static {
        MY_LUCKY_BLOCK = register("my_lucky_block", new LuckyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5f).explosionResistance(5000000.0f)));
        DEBUG_LUCKY_BLOCK = register("debug_lucky_block", new LuckyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(0.5f).explosionResistance(5000000.0f), "test", true));
    }

    public static <T extends Block> T register(String path, T block) {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(MyLuckyBlock.MOD_ID, path), block);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MyLuckyBlock.MOD_ID, path), new BlockItem(block, new Item.Properties()));
        return block;
    }

    public static void registerModBlocks() {
    }
}
