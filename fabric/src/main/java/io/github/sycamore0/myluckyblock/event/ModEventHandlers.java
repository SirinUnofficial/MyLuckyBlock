package io.github.sycamore0.myluckyblock.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

public class ModEventHandlers {
    public static void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> BreakLuckyBlock.breakLuckyBlock(world, player, pos, state));
    }
}