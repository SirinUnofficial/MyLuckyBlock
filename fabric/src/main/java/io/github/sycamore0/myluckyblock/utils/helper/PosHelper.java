package io.github.sycamore0.myluckyblock.utils.helper;


import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class PosHelper {
    public static Vec3 parseBlockPos(BlockPos blockPos) {
        return blockPos.getBottomCenter();
    }

    public static BlockPos parseVec3d(Vec3 vec3) {
        return new BlockPos((int)(vec3.x() - 0.5), (int)vec3.y(), (int)(vec3.z() - 0.5));
    }

    public static Vec3 calcOffset(Vec3 pos, Vec3 offset) {
        return new Vec3(pos.x() + offset.x(), pos.y() + offset.y(), pos.z() + offset.z());
    }
}
