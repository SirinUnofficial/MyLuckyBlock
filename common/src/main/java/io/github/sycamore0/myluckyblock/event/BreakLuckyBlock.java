package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.utils.*;
import io.github.sycamore0.myluckyblock.utils.helper.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BreakLuckyBlock {
    public static LuckyEventDataManager manager = new LuckyEventDataManager();

    public static void breakLuckyBlock(Level level, @Nullable Player player, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel && state.getBlock() instanceof LuckyBlock luckyBlock) {
            if (player != null) {
                // Check silk touch
                boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
                if (hasSilkTouch) {
                    dropSelf(serverLevel, pos, luckyBlock);
                    return;
                }
            }

            String eventPackId = luckyBlock.getEventPackId();
            boolean includeBuiltIn = luckyBlock.includeBuiltIn();

            if (!manager.isLoaded(eventPackId)) {
                manager.loadEvents(eventPackId, includeBuiltIn);
            }

            // Trigger random event
            RandomEventReader event = manager.getRandomEvent(eventPackId);
            if (event != null) {
                LuckyEventExecutor.executeLuckyFunction(serverLevel, player, pos, event);
            }
        }
    }

    private static void dropSelf(ServerLevel serverLevel, BlockPos pos, LuckyBlock luckyBlock) {
        serverLevel.addFreshEntity(new ItemEntity(serverLevel, pos.getCenter().x, pos.getY(), pos.getCenter().z, new ItemStack(luckyBlock)));
    }
}