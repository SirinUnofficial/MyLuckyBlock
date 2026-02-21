package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class AddonUtil {
    public static Block createLB(String blockId, String modId, String eventPackGroupName) {
        return createLB(blockId, modId, eventPackGroupName, false, MapColor.COLOR_YELLOW, 0.5f, 5000000.0f);
    }

    public static Block createLB(String blockId, String modId, String eventPackGroupName, boolean includeBuiltIn) {
        return createLB(blockId, modId, eventPackGroupName, includeBuiltIn, MapColor.COLOR_YELLOW, 0.5f, 5000000.0f);
    }

    public static Block createLB(String blockId, String modId, String eventPackGroupName, boolean includeBuiltIn, MapColor mapColor) {
        return createLB(blockId, modId, eventPackGroupName, includeBuiltIn, mapColor, 0.5f, 5000000.0f);
    }

    public static Block createLB(String blockId, String modId, String eventPackGroupName, boolean includeBuiltIn, MapColor mapColor, float strength, float explosionResistance) {
        final Block ADDON_LUCKY_BLOCK = ModBlocks.createNewLuckyBlock(blockId, modId, eventPackGroupName, includeBuiltIn, mapColor, strength, explosionResistance);
        ModItemGroups.addBlockToGroup(new ItemStack(ADDON_LUCKY_BLOCK));
        return ADDON_LUCKY_BLOCK;
    }
}
