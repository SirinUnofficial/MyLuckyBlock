package io.github.sycamore0.myluckyblock.lucky.action;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.spec.RandomNum;
import io.github.sycamore0.myluckyblock.lucky.action.spec.Vector3;
import io.github.sycamore0.myluckyblock.utils.helper.DataComponentsHelper;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public final class ActionSupport {
    private ActionSupport() {
    }

    public static int roll(boolean useRandom, @Nullable RandomNum range, int fallback) {
        if (!useRandom || range == null) {
            return fallback;
        }
        int min = range.getMin();
        int max = range.getMax();
        try {
            return min + ThreadLocalRandom.current().nextInt(max - min + 1);
        } catch (IllegalArgumentException e) {
            Constants.LOG.error("Invalid random range [{}, {}]: {}", min, max, e.getMessage());
            return fallback;
        }
    }

    public static Vec3 spread(ServerLevel level, Vec3 pos, @Nullable Vector3 spread) {
        if (spread == null) {
            return pos;
        }
        double x = Math.abs(spread.getX());
        double y = Math.abs(spread.getY());
        double z = Math.abs(spread.getZ());
        if (x == 0.0 && y == 0.0 && z == 0.0) {
            return pos;
        }
        return pos.add(
                Mth.randomBetween(level.getRandom(), (float) -x, (float) x),
                Mth.randomBetween(level.getRandom(), (float) -y, (float) y),
                Mth.randomBetween(level.getRandom(), (float) -z, (float) z));
    }

    /**
     * Applies {@code nbt} and {@code data_components} to the block entity already present at
     * {@code blockPos}. Both are optional; {@code nbt} is applied first so it wins on conflicts.
     */
    public static void applyBlockEntityData(ServerLevel serverLevel, BlockPos blockPos, String blockId,
                                            @Nullable String nbtString,
                                            @Nullable JsonObject dataComponents) {
        boolean hasComponents = dataComponents != null && !dataComponents.isEmpty();
        boolean hasNbt = nbtString != null && !nbtString.isBlank();
        if (!hasComponents && !hasNbt) {
            return;
        }

        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        if (blockEntity == null) {
            Constants.LOG.warn("Block {} has no block entity, ignoring 'nbt' and 'data_components'", blockId);
            return;
        }

        if (hasNbt) {
            CompoundTag nbt;
            try {
                nbt = NbtHelper.generateNbt(nbtString);
            } catch (IllegalArgumentException e) {
                Constants.LOG.warn("Failed to parse 'nbt' for block {}: {}", blockId, e.getMessage());
                nbt = null;
            }
            if (nbt != null) {
                blockEntity.loadWithComponents(nbt, serverLevel.registryAccess());
            }
        }

        if (hasComponents) {
            Block block = serverLevel.getBlockState(blockPos).getBlock();
            DataComponentsHelper.applyBlockEntityComponents(serverLevel, blockEntity, block, blockId,
                    dataComponents);
        }

        blockEntity.setChanged();
        BlockState blockState = serverLevel.getBlockState(blockPos);
        serverLevel.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL);
    }
}
