package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class AddonUtil {
    public static void createLB(String blockId, String modId, String eventPackId) {
        createLB(blockId, modId, eventPackId, false, MapColor.COLOR_YELLOW, 0.5f, 5000000.0f);
    }

    public static void createLB(String blockId, String modId, String eventPackId, boolean includeBuiltIn) {
        createLB(blockId, modId, eventPackId, includeBuiltIn, MapColor.COLOR_YELLOW, 0.5f, 5000000.0f);
    }

    public static void createLB(String blockId, String modId, String eventPackId, boolean includeBuiltIn, MapColor mapColor) {
        createLB(blockId, modId, eventPackId, includeBuiltIn, mapColor, 0.5f, 5000000.0f);
    }

    public static void createLB(String blockId, String modId, String eventPackId, boolean includeBuiltIn, MapColor mapColor, float strength, float explosionResistance) {
        CommonClass.addEventPackId(eventPackId);
        final Block ADDON_LUCKY_BLOCK = ModBlocks.createNewLuckyBlock(blockId, modId, eventPackId, includeBuiltIn, mapColor, strength, explosionResistance);
        ModItemGroups.addBlockToGroup(new ItemStack(ADDON_LUCKY_BLOCK));
    }
}
