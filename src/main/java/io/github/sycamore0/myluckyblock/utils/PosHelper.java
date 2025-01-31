package io.github.sycamore0.myluckyblock.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PosHelper {
    public static Vec3d parseBlockPos(BlockPos blockPos) {
        return new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
    }

    public static BlockPos parseVec3d(Vec3d vec3d) {
        return new BlockPos((int)(vec3d.getX() - 0.5), (int)vec3d.getY(), (int)(vec3d.getZ() - 0.5));
    }
}
