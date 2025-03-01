package io.github.sycamore0.myluckyblock.utils.helper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PosHelper {
    public static Vec3d parseBlockPos(BlockPos blockPos) {
        return blockPos.toBottomCenterPos();
    }

    public static BlockPos parseVec3d(Vec3d vec3d) {
        return new BlockPos((int)(vec3d.getX() - 0.5), (int)vec3d.getY(), (int)(vec3d.getZ() - 0.5));
    }

    public static Vec3d calcOffset(Vec3d pos, Vec3d offset) {
        return new Vec3d(pos.getX() + offset.getX(), pos.getY() + offset.getY(), pos.getZ() + offset.getZ());
    }
}
