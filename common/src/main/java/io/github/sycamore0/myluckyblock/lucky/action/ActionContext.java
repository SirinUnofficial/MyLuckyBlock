package io.github.sycamore0.myluckyblock.lucky.action;

import io.github.sycamore0.myluckyblock.lucky.LuckyEventExecutor;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record ActionContext(ServerLevel level, @Nullable Player player, BlockPos pos) {
    public Vec3 resolve(PosSrc posSrc, Vec3 offset) {
        return LuckyEventExecutor.resolvePosition(pos, player, posSrc, offset);
    }
}
