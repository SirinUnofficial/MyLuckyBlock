package io.github.sycamore0.myluckyblock.lucky.action;

import io.github.sycamore0.myluckyblock.utils.PosSrc;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record ActionContext(ServerLevel level, @Nullable Player player, BlockPos pos) {
    public Vec3 resolvePos(PosSrc posSrc, Vec3 offset) {
        Vec3 base = posSrc == PosSrc.PLAYER && player != null ? player.position() : PosHelper.parseBlockPos(pos);
        return PosHelper.calcOffset(base, offset);
    }
}
