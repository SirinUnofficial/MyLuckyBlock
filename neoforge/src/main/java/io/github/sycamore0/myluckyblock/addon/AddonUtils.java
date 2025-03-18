package io.github.sycamore0.myluckyblock.addon;

import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class AddonUtils {
    // createLB() not exist yet.

    public static void addToGroup(Block block) {
        ModItemGroups.MYLUCKYBLOCK_GROUP.get().getDisplayItems().add(new ItemStack(block));
    }
}
