package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.LuckyBlockEntity;
import io.github.sycamore0.myluckyblock.event.listener.LuckyEventsReloadListener;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModEventHandlers {
    private static void onBreakBlock(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(blockState.getBlock() instanceof LuckyBlock)) {
            return;
        }

        LuckyBlockData luckyBlockData = blockEntity instanceof LuckyBlockEntity luckyBlockEntity ? luckyBlockEntity.toData() : LuckyBlockData.DEFAULT;

        BreakLuckyBlock.breakLuckyBlock(serverLevel, player, blockPos, blockState, luckyBlockData);
    }

    public static void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register(ModEventHandlers::onBreakBlock);

        LuckyEventsReloadListener reloadListener = new LuckyEventsReloadListener();
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(reloadListener);
    }
}