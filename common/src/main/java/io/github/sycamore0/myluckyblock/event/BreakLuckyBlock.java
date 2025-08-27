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

public class BreakLuckyBlock {
    public static LuckyEventDataManager manager = new LuckyEventDataManager();

    public static void breakLuckyBlock(Level level, Player player, BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel && state.getBlock() instanceof LuckyBlock luckyBlock) {
            // Check silk touch
            boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
            if (hasSilkTouch) {
                level.addFreshEntity(new ItemEntity(level, pos.getCenter().x, pos.getY(), pos.getCenter().z, new ItemStack(luckyBlock)));
                return;
            }
            String eventPackId = luckyBlock.getEventPackId();
            boolean includeBuiltIn = luckyBlock.includeBuiltIn();

            if (!manager.isLoaded(eventPackId)) {
                manager.loadEvents(eventPackId, includeBuiltIn);
            }

            // Trigger random event
            RandomEventReader event = manager.getRandomEvent(eventPackId);
            if (event != null) {
                if (level instanceof ServerLevel serverLevel) {
                    LuckyEventExecutor.executeLuckyFunction(serverLevel, player, pos, event);
                }
            }
        }
    }
}