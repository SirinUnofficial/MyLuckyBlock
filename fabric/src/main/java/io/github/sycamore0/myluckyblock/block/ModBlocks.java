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
        MY_LUCKY_BLOCK = register("my_lucky_block", LuckyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5f).explosionResistance(5000000.0f), Constants.MOD_ID);
    }

    public static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings, String modId) {
        final ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modId, path);
        final ResourceKey<Block> registryKey = ResourceKey.create(Registries.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.registerBlock(block);
        return block;
    }

    public static Block createNewLuckyBlock(String blockId, String modId, String eventPackId, boolean includeBuiltIn, MapColor mapColor, float strength, float explosionResistance) {
        return register(blockId,
                (settings) -> new LuckyBlock(settings, eventPackId, includeBuiltIn),
                BlockBehaviour.Properties.of().mapColor(mapColor).strength(strength).explosionResistance(explosionResistance),
                modId);
    }

    public static void onInitialize() {
    }
}
