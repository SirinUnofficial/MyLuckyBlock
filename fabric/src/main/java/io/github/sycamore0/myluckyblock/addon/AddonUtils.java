package io.github.sycamore0.myluckyblock.addon;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AddonUtils {
    // Example Code
    // CommonClass.addModId("newluckyblock");
    // public static final Block NEW_LUCKY_BLOCK = createNewLB("new_lucky_block", "newluckyblock");
    // public static void test() {
    //     addToGroup(NEW_LUCKY_BLOCK);
    // }

    public static Block createNewLB(String blockId, String modId) {
        return createNewLB(blockId, modId, MapColor.COLOR_PINK, false);
    }

    public static Block createNewLB(String blockId, String modId, boolean includeBuiltIn) {
        return createNewLB(blockId, modId, MapColor.COLOR_PINK, includeBuiltIn);
    }

    public static Block createNewLB(String blockId, String modId, MapColor color, boolean includeBuiltIn) {
        return ModBlocks.register(blockId, new LuckyBlock(BlockBehaviour.Properties.of().mapColor(color).strength(0.5f).explosionResistance(5000000.0f), modId, includeBuiltIn), modId);
    }

    public static void addToGroup(Block block) {
        ModItemGroups.MYLUCKYBLOCK_GROUP.getDisplayItems().add(new ItemStack(block));
    }
}
