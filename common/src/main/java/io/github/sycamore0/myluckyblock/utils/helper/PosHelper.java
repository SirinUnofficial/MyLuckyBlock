package io.github.sycamore0.myluckyblock.utils.helper;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class PosHelper {
    public static Vec3 parseBlockPos(BlockPos blockPos) {
        return getBottomCenter(blockPos);
    }

    public static BlockPos parseVec3d(Vec3 vec3) {
        return new BlockPos((int) (vec3.x() - 0.5), (int) vec3.y(), (int) (vec3.z() - 0.5));
    }

    public static Vec3 calcOffset(Vec3 pos, Vec3 offset) {
        return new Vec3(pos.x() + offset.x(), pos.y() + offset.y(), pos.z() + offset.z());
    }

    // funName just for check error logs
    public static Vec3 calcPos(Vec3 blockPos, Vec3 playerPos, PosSrc posSrc, Vec3 offset, String funName) {
        Vec3 targetPos = blockPos;
        switch (posSrc) {
            case PosSrc.BLOCK:
                targetPos = calcOffset(blockPos, offset);
                break;
            case PosSrc.PLAYER:
                targetPos = calcOffset(playerPos, offset);
                break;
            default:
                Constants.LOG.error("Error: {} Invalid Pos Src: {}", funName, posSrc);
                break;
        }
        return targetPos;
    }

    // for 26.2+
    public static Vec3 getCenter(BlockPos blockPos) {
        return Vec3.atCenterOf(blockPos);
    }

    public static Vec3 getBottomCenter(BlockPos blockPos) {
        return Vec3.atBottomCenterOf(blockPos);
    }
}
