package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    private ModBlocks() {
    }

    public static final Block MY_LUCKY_BLOCK;
    public static final Block DEBUG_LUCKY_BLOCK;

    static {
        MY_LUCKY_BLOCK = register("my_lucky_block", new LuckyBlock(Block.Settings.create().mapColor(MapColor.GOLD).strength(0.5f).resistance(5000000.0f)));
        DEBUG_LUCKY_BLOCK = register("debug_lucky_block", new Block(Block.Settings.create().mapColor(MapColor.GOLD).strength(0.5f).resistance(5000000.0f)));
    }

    private static <T extends Block> T register(String path, T block) {
        Registry.register(Registries.BLOCK, Identifier.of(MyLuckyBlock.MOD_ID, path), block);
        Registry.register(Registries.ITEM, Identifier.of(MyLuckyBlock.MOD_ID, path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void registerModBlocks() {
    }
}
