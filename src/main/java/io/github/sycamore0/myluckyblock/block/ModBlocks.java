package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModBlocks {
    private ModBlocks() {
    }

    public static final Block MY_LUCKY_BLOCK;
    public static final Block DEBUG_LUCKY_BLOCK;

    static {
        MY_LUCKY_BLOCK = register("my_lucky_block", LuckyBlock::new, Block.Settings.create().mapColor(MapColor.GOLD).strength(0.5f).resistance(5000000.0f));
        DEBUG_LUCKY_BLOCK = register("debug_lucky_block", Block::new, Block.Settings.create().mapColor(MapColor.GOLD).strength(0.5f).resistance(5000000.0f));
    }

    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = Identifier.of(MyLuckyBlock.MOD_ID, path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }

    public static void registerModBlocks() {
    }
}
