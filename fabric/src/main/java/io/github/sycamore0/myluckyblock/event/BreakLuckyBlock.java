package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.utils.*;
import io.github.sycamore0.myluckyblock.utils.helper.EnchantmentsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BreakLuckyBlock {
    public static LuckyDataManager manager = new LuckyDataManager();

    public static void breakLuckyBlock(Level level, Player player, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel && state.getBlock() instanceof LuckyBlock luckyBlock) {
            // Check silk touch
            boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
            if (hasSilkTouch) {
                level.addFreshEntity(new ItemEntity(level, pos.getCenter().x, pos.getY(), pos.getCenter().z(), new ItemStack(luckyBlock)));
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
                LuckyExecutor.executeLuckyFunction(level, player, pos, event);
            }

            if (state.getBlock() == ModBlocks.DEBUG_LUCKY_BLOCK) {
                // Trigger all events
                for (int i = 1; i <= manager.getRandomEventsCount(modId); i++) {
                    LuckyEventReader event1 = manager.getEventById(modId, i);
                    LuckyExecutor.executeLuckyFunction(level, player, pos, event1);
                }
            }
        }
    }
}