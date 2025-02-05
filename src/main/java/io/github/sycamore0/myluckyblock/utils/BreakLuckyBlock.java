package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakLuckyBlock {
    public static LuckyDataManager manager = new LuckyDataManager();

    public static void breakLuckyBlock(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld && state.getBlock() instanceof LuckyBlock luckyBlock) {
            // check silk touch
            boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
            if (hasSilkTouch) {
                LuckyFunctions.dropItems(world, PosHelper.parseBlockPos(pos), new ItemStack(luckyBlock));
                return;
            }
            String modId = luckyBlock.getModId();
            boolean includeBuiltIn = luckyBlock.includeBuiltIn();

            if (!manager.isLoaded(modId)) {
                manager.loadEvents(modId, includeBuiltIn);
            }

            // execute random event
            LuckyEventReader event = manager.getRandomEvent(modId);
            if (event != null) {
                LuckyExecutor.executeLuckyFunction(world, player, pos, event);
            }
        }
    }
}