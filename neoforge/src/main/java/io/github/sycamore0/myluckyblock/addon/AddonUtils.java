package io.github.sycamore0.myluckyblock.addon;

import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

public class AddonUtils {
    // Example Code
    // CommonClass.addModId("newluckyblock");
    // final Block NEW_LUCKY_BLOCK = AddonUtils.createNewLB("new_lucky_block", "newluckyblock");
    // AddonUtils.addToGroup(NEW_LUCKY_BLOCK);

    public static Block createNewLB(String blockId, String modId) {
        return createNewLB(blockId, modId, MapColor.COLOR_PINK, false);
    }

    public static Block createNewLB(String blockId, String modId, boolean includeBuiltIn) {
        return createNewLB(blockId, modId, MapColor.COLOR_PINK, includeBuiltIn);
    }

    public static Block createNewLB(String blockId, String modId, MapColor color, boolean includeBuiltIn) {
        // TODO
        return null;
    }

    public static void addToGroup(Block block) {
        ModItemGroups.MYLUCKYBLOCK_GROUP.get().getDisplayItems().add(new ItemStack(block));
    }
}
