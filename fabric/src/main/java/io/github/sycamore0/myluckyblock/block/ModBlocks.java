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
        MY_LUCKY_BLOCK = createNewLB("my_lucky_block", Constants.MOD_ID, true);
    }

    private static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings, String modId) {
        final ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(modId, path);
        final ResourceKey<Block> registryKey = ResourceKey.create(Registries.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.registerBlock(block);
        return block;
    }

    public static Block createNewLB(String blockId, String modId) {
        return createNewLB(blockId, modId, MapColor.COLOR_PINK, false);
    }

    public static Block createNewLB(String blockId, String modId, boolean includeBuiltIn) {
        return createNewLB(blockId, modId, MapColor.COLOR_PINK, includeBuiltIn);
    }

    public static Block createNewLB(String blockId, String modId, MapColor color, boolean includeBuiltIn) {
        return register(blockId, (props) -> new LuckyBlock(props, modId, includeBuiltIn), BlockBehaviour.Properties.of().mapColor(color).strength(0.5f).explosionResistance(5000000.0f), modId);
    }

    public static void registerModBlocks() {
    }
}
