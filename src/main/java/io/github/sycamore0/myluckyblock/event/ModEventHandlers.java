package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.utils.*;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.MinecraftVersion;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ModEventHandlers {
    private static boolean isFirstTime = true;

    public static void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
                    checkSnapshot(player);
                    BreakLuckyBlock.breakLuckyBlock(world, player, pos, state);
                }
        );
    }

    private static void checkSnapshot(PlayerEntity player) {
        if (!MinecraftVersion.CURRENT.isStable() && isFirstTime) {
            String snapshot_warning = Text.translatable("myluckyblock.message.snapshot_warning").getString();
            LuckyFunctions.sendMessage(player, snapshot_warning, 1);
            isFirstTime = false;
        }
    }
}