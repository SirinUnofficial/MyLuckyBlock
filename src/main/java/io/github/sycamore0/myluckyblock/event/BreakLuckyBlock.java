package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.utils.*;
import io.github.sycamore0.myluckyblock.utils.helper.EnchantmentsHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakLuckyBlock {
    public static LuckyDataManager manager = new LuckyDataManager();

    public static void breakLuckyBlock(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld && state.getBlock() instanceof LuckyBlock luckyBlock) {
            // Check silk touch
            boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
            if (hasSilkTouch) {
                LuckyFunctions.dropItems(world, pos.toCenterPos(), "myluckyblock:my_lucky_block", 1);
                return;
            }
            String modId = luckyBlock.getModId();
            boolean includeBuiltIn = luckyBlock.includeBuiltIn();

            if (!manager.isLoaded(modId)) {
                manager.loadEvents(modId, includeBuiltIn);
            }

            // Trigger random event
            LuckyEventReader event = manager.getRandomEvent(modId);
            if (event != null) {
                LuckyExecutor.executeLuckyFunction(world, player, pos, event);
            }

            if (state.getBlock() == ModBlocks.DEBUG_LUCKY_BLOCK) {
                // Trigger all events
                for (int i = 1; i <= manager.getRandomEventsCount(modId); i++) {
                    LuckyEventReader event1 = manager.getEventById(modId, i);
                    LuckyExecutor.executeLuckyFunction(world, player, pos, event1);
                }
            }
        }
    }
}