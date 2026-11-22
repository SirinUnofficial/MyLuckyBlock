package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.LuckyBlockEntity;
import io.github.sycamore0.myluckyblock.event.listener.LuckyEventsReloadListener;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class ModEventHandlers {
    @SubscribeEvent
    private static void onDestroyedByPlayer(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getState();

        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!(blockState.getBlock() instanceof LuckyBlock)) return;

        LuckyBlockData luckyBlockData = serverLevel.getBlockEntity(blockPos) instanceof LuckyBlockEntity luckyBlockEntity ? luckyBlockEntity.toData() : LuckyBlockData.DEFAULT;

        LuckyBlockTrigger.breakLuckyBlock(serverLevel, player, blockPos, blockState, luckyBlockData);

        event.setCanceled(true);

        ItemStack tool = player.getMainHandItem();
        tool.mineBlock(serverLevel, blockState, blockPos, player);
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        LuckyEventsReloadListener listener = new LuckyEventsReloadListener();
        event.addListener(listener);
        LuckyBlockTrigger.manager = new LuckyEventDataManager(listener);
    }
}