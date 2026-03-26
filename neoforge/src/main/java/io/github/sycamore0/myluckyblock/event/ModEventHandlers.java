package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.event.listener.LuckyEventsReloadListener;
import io.github.sycamore0.myluckyblock.utils.LuckyEventDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public class ModEventHandlers {
    @SubscribeEvent
    private static void onDestroyedByPlayer(BreakBlockEvent event) {
        Player player = event.getPlayer();
        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getState();

        if (event.getLevel() instanceof ServerLevel serverLevel) {
            // if lucky block
            if (blockState.getBlock() instanceof LuckyBlock) {
                serverLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());

                BreakLuckyBlock.breakLuckyBlock(serverLevel, player, blockPos, blockState);
                event.setCanceled(true);

                // cost durability
                ItemStack tool = player.getMainHandItem();
                if (tool.isDamageableItem()) {
                    int damage = blockState.getDestroySpeed(serverLevel, blockPos) > 0 ? 1 : 0;
                    tool.hurtAndBreak(damage, player, player.getEquipmentSlotForItem(tool));
                }
                player.awardStat(Stats.BLOCK_MINED.get(blockState.getBlock()));
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        LuckyEventsReloadListener listener = new LuckyEventsReloadListener();
        event.addListener(Identifier.parse("lucky_loader"), listener);
        BreakLuckyBlock.manager = new LuckyEventDataManager(listener);
    }
}