package io.github.sycamore0.myluckyblock.utils.helper;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class PosHelper {
    public static Vec3 parseBlockPos(BlockPos blockPos) {
        return blockPos.getBottomCenter();
    }

    public static BlockPos parseVec3d(Vec3 vec3) {
        return BlockPos.containing(vec3);
    }

    public static Vec3 calcOffset(Vec3 pos, Vec3 offset) {
        return new Vec3(pos.x() + offset.x(), pos.y() + offset.y(), pos.z() + offset.z());
    }
}
