package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class ModBlocks {
    public static final Block MY_LUCKY_BLOCK;

    static {
        MY_LUCKY_BLOCK = register("my_lucky_block", LuckyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(0.5f).explosionResistance(5000000.0f));
    }

    private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings) {
        final ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
        final ResourceKey<Block> registryKey = ResourceKey.create(Registries.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.registerBlock(block);
        return block;
    }

    public static void registerModBlocks() {
    }
}
