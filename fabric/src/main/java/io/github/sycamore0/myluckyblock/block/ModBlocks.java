package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
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

    static {
        MY_LUCKY_BLOCK = register("my_lucky_block", new LuckyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5f).explosionResistance(5000000.0f)), Constants.MOD_ID);
    }

    public static <T extends Block> T register(String path, T block, String modId) {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(modId, path), block);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(modId, path), new BlockItem(block, new Item.Properties()));
        return block;
    }

    public static LuckyBlock createNewLuckyBlock(String blockId, String modId, String eventPackGroupName, boolean includeBuiltIn, MapColor mapColor, float strength, float explosionResistance) {
        return register(blockId, new LuckyBlock(BlockBehaviour.Properties.of().mapColor(mapColor).strength(strength).explosionResistance(explosionResistance), eventPackGroupName, includeBuiltIn), modId);
    }

    public static void onInitialize() {
    }
}
