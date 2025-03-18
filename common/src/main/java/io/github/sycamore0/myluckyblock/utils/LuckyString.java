package io.github.sycamore0.myluckyblock.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class LuckyString {
    // WIP
    public static String process(String str, Player player, BlockPos blockPos) {
        // Blocks
        str = str.replace("%blockX%", String.valueOf(blockPos.getX()));
        str = str.replace("%blockY%", String.valueOf(blockPos.getY()));
        str = str.replace("%blockZ%", String.valueOf(blockPos.getZ()));

        // Players
        str = str.replace("%playerName%", player.getName().getString());
        str = str.replace("%playerX%", String.valueOf(player.getX()));
        str = str.replace("%playerY%", String.valueOf(player.getY()));
        str = str.replace("%playerZ%", String.valueOf(player.getZ()));

        return str;
    }
}
