package io.github.sycamore0.myluckyblock.lucky.action;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.LuckyBlockEntity;
import io.github.sycamore0.myluckyblock.block.ModBlocksCommon;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventFunctions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

final class WellAction {
    static final String OUTCOME_GROUP = "myluckywell";
    static final String OUTCOME_PACK = OUTCOME_GROUP + ":outcomes";
    private static final List<String> OUTCOMES = List.of(OUTCOME_PACK + "#1", OUTCOME_PACK + "#2");

    private static final int RADIUS = 1;
    private static final int WATER_LEVEL = 2;
    private static final int FENCE_BOTTOM = WATER_LEVEL + 1;
    private static final int FENCE_HEIGHT = 2;
    private static final int LID_LEVEL = FENCE_BOTTOM + FENCE_HEIGHT;

    private static final String DEFAULT_WALL_BLOCK = "minecraft:cobblestone";
    private static final String DEFAULT_FENCE_BLOCK = "minecraft:cherry_fence";
    private static final String DEFAULT_LID_BLOCK = "minecraft:cherry_stairs";
    private static final String DEFAULT_GLASS_BLOCK = "minecraft:pink_stained_glass";
    private static final String DEFAULT_PLATE_BLOCK = "minecraft:oak_pressure_plate";
    private static final String DEFAULT_WATER_BLOCK = "minecraft:water";

    private WellAction() {
    }

    static void execute(ActionContext context, JsonObject step) {
        WellPayload payload = LuckyActions.payloadObject(step, WellPayload.class);
        if (payload == null) {
            payload = WellPayload.DEFAULT;
        }

        ServerLevel level = context.level();
        BlockPos luckyPos = context.pos().below(2);

        Block wall = resolveBlock(payload.wallBlock(), DEFAULT_WALL_BLOCK, "wall_block");
        Block fence = resolveBlock(payload.fenceBlock(), DEFAULT_FENCE_BLOCK, "fence_block");
        Block lid = resolveBlock(payload.lidBlock(), DEFAULT_LID_BLOCK, "lid_block");
        Block glass = resolveBlock(payload.glassBlock(), DEFAULT_GLASS_BLOCK, "glass_block");
        Block plate = resolveBlock(payload.plateBlock(), DEFAULT_PLATE_BLOCK, "plate_block");
        Block water = resolveBlock(payload.waterBlock(), DEFAULT_WATER_BLOCK, "water_block");

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                for (int dy = 0; dy <= WATER_LEVEL; dy++) {
                    level.setBlockAndUpdate(luckyPos.offset(dx, dy, dz), wall.defaultBlockState());
                }
            }
        }

        for (int dx = -RADIUS; dx <= RADIUS; dx += 2 * RADIUS) {
            for (int dz = -RADIUS; dz <= RADIUS; dz += 2 * RADIUS) {
                for (int i = 0; i < FENCE_HEIGHT; i++) {
                    level.setBlockAndUpdate(luckyPos.offset(dx, FENCE_BOTTOM + i, dz),
                            fence.defaultBlockState());
                }
            }
        }

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                boolean center = dx == 0 && dz == 0;
                level.setBlockAndUpdate(luckyPos.offset(dx, LID_LEVEL, dz),
                        center ? glass.defaultBlockState() : lidState(lid, dx, dz));
            }
        }

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                BlockPos lidPos = luckyPos.offset(dx, LID_LEVEL, dz);
                BlockState current = level.getBlockState(lidPos);
                BlockState corrected = Block.updateFromNeighbourShapes(current, level, lidPos);
                if (corrected != current) {
                    level.setBlock(lidPos, corrected, Block.UPDATE_CLIENTS);
                }
            }
        }

        LuckyBlockSpec spec = payload.luckyBlock() == null ? LuckyBlockSpec.DEFAULT : payload.luckyBlock();
        Block luckyBlock = resolveLuckyBlock(spec.id());
        level.setBlockAndUpdate(luckyPos, luckyBlock.defaultBlockState());

        BlockEntity blockEntity = level.getBlockEntity(luckyPos);
        if (!(blockEntity instanceof LuckyBlockEntity luckyBlockEntity)) {
            Constants.LOG.warn("Lucky well: {} has no lucky block entity, aborting", luckyPos);
            return;
        }

        luckyBlockEntity.applyData(new LuckyBlockData(0, true, OUTCOMES));
        if (spec.hasOverrides()) {
            String blockId = spec.id() != null && !spec.id().isBlank()
                    ? spec.id()
                    : BuiltInRegistries.BLOCK.getKey(luckyBlock).toString();
            LuckyEventFunctions.applyBlockEntityData(level, luckyPos, blockId, spec.nbt(), spec.dataComponents());
        }

        level.setBlockAndUpdate(luckyPos.above(1), plate.defaultBlockState());
        level.setBlockAndUpdate(luckyPos.above(2), water.defaultBlockState());

        LuckyEventDataManager manager = BreakLuckyBlock.manager;
        if (manager != null && !manager.isLoaded(OUTCOME_GROUP)) {
            manager.loadEvents(OUTCOME_GROUP, false);
        }
    }

    private static BlockState lidState(Block lid, int dx, int dz) {
        BlockState state = lid.defaultBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facingTowardsCenter(dx, dz));
        }
        return state;
    }

    private static Direction facingTowardsCenter(int dx, int dz) {
        if (dx == 0 && dz == 0) {
            return Direction.NORTH;
        }
        if (Math.abs(dx) != Math.abs(dz)) {
            return Math.abs(dx) > Math.abs(dz)
                    ? (dx > 0 ? Direction.WEST : Direction.EAST)
                    : (dz > 0 ? Direction.NORTH : Direction.SOUTH);
        }
        return dx > 0 ? Direction.WEST : Direction.EAST;
    }

    private static Block resolveBlock(@Nullable String configured, String fallback, String field) {
        Block block = lookupBlock(configured);
        if (block != null) {
            return block;
        }
        if (configured != null && !configured.isBlank()) {
            Constants.LOG.warn("Lucky well: unknown {} '{}', using '{}'", field, configured, fallback);
        }
        Block fallbackBlock = lookupBlock(fallback);
        if (fallbackBlock == null) {
            Constants.LOG.error("Lucky well: fallback {} '{}' is not registered either, using cobblestone", field, fallback);
            return Blocks.COBBLESTONE;
        }
        return fallbackBlock;
    }

    private static Block resolveLuckyBlock(@Nullable String configured) {
        if (configured == null || configured.isBlank()) {
            return ModBlocksCommon.luckyBlock();
        }
        Block block = lookupBlock(configured);
        if (block == null) {
            Constants.LOG.warn("Lucky well: unknown lucky_block '{}', using the built-in lucky block", configured);
            return ModBlocksCommon.luckyBlock();
        }
        if (!(block instanceof LuckyBlock)) {
            Constants.LOG.warn("Lucky well: '{}' is not a lucky block, using the built-in lucky block", configured);
            return ModBlocksCommon.luckyBlock();
        }
        return block;
    }

    @Nullable
    private static Block lookupBlock(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return null;
        }
        Block block = BuiltInRegistries.BLOCK.get(location);
        return block == Blocks.AIR ? null : block;
    }

    record WellPayload(
            @SerializedName("lucky_block") @Nullable LuckyBlockSpec luckyBlock,
            @SerializedName("wall_block") @Nullable String wallBlock,
            @SerializedName("fence_block") @Nullable String fenceBlock,
            @SerializedName("lid_block") @Nullable String lidBlock,
            @SerializedName("glass_block") @Nullable String glassBlock,
            @SerializedName("plate_block") @Nullable String plateBlock,
            @SerializedName("water_block") @Nullable String waterBlock) {
        static final WellPayload DEFAULT = new WellPayload(null, null, null, null, null, null, null);
    }

    record LuckyBlockSpec(
            @SerializedName("id") @Nullable String id,
            @SerializedName("nbt") @Nullable String nbt,
            @SerializedName("data_components") @Nullable JsonObject dataComponents) {
        static final LuckyBlockSpec DEFAULT = new LuckyBlockSpec(null, null, null);

        boolean hasOverrides() {
            return (nbt != null && !nbt.isBlank()) || (dataComponents != null && !dataComponents.isEmpty());
        }
    }
}
