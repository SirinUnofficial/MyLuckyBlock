package io.github.sycamore0.myluckyblock.lucky.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.spec.RandomNum;
import io.github.sycamore0.myluckyblock.lucky.action.spec.Vector3;
import io.github.sycamore0.myluckyblock.utils.helper.DataComponentsHelper;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
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

    public static void applyBlockEntityData(ServerLevel serverLevel, BlockPos blockPos, String blockId, @Nullable String nbtString, @Nullable JsonObject dataComponentsElement) {
        boolean hasNbt = nbtString != null && !nbtString.isBlank();
        boolean hasDataComponents = dataComponentsElement != null && !dataComponentsElement.isEmpty();
        if (!hasNbt && !hasDataComponents) {
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

        if (hasDataComponents) {
            Block block = serverLevel.getBlockState(blockPos).getBlock();
            DataComponentsHelper.applyToBlockEntity(serverLevel, blockEntity, block, blockId, dataComponentsElement);
        }

        blockEntity.setChanged();
        BlockState blockState = serverLevel.getBlockState(blockPos);
        serverLevel.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL);
    }

    @Nullable
    public static BlockState resolveBlockState(@Nullable String blockId, @Nullable JsonElement blockStateElement) {
        if (blockId == null || blockId.isBlank()) {
            Constants.LOG.warn("Missing block id, skipping");
            return null;
        }

        String id = blockId;
        JsonObject inline = null;
        int bracket = blockId.indexOf('[');
        if (bracket >= 0) {
            int end = blockId.lastIndexOf(']');
            if (end > bracket) {
                id = blockId.substring(0, bracket);
                inline = parseInlineState(blockId.substring(bracket + 1, end));
            }
        }

        ResourceLocation location;
        try {
            location = ResourceLocation.parse(id.trim());
        } catch (Exception e) {
            Constants.LOG.warn("Invalid block id '{}': {}", blockId, e.getMessage());
            return null;
        }

        Block block = BuiltInRegistries.BLOCK.get(location);
        if (block == Blocks.AIR && !"minecraft:air".equals(location.toString())) {
            Constants.LOG.warn("Unknown block id '{}', skipping", blockId);
            return null;
        }

        BlockState blockState = block.defaultBlockState();
        blockState = applyState(blockState, inline);
        return applyState(blockState, toProperties(blockStateElement));
    }

    @Nullable
    private static JsonObject toProperties(@Nullable JsonElement blockState) {
        if (blockState == null || blockState.isJsonNull()) {
            return null;
        }
        if (blockState.isJsonObject()) {
            return blockState.getAsJsonObject();
        }
        if (blockState.isJsonPrimitive() && blockState.getAsJsonPrimitive().isString()) {
            String text = blockState.getAsString().trim();
            if (text.startsWith("[") && text.endsWith("]")) {
                text = text.substring(1, text.length() - 1);
            }
            return parseInlineState(text);
        }
        Constants.LOG.warn("Ignoring 'block_state': expected an object or a 'key=value,...' string, got {}", blockState);
        return null;
    }

    private static BlockState applyState(BlockState blockState, @Nullable JsonObject properties) {
        if (properties == null || properties.isEmpty()) {
            return blockState;
        }
        for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
            JsonElement value = entry.getValue();
            if (value == null || !value.isJsonPrimitive()) {
                Constants.LOG.warn("Ignoring block state property '{}': expected a plain value, got {}", entry.getKey(), value);
                continue;
            }
            blockState = applyProperty(blockState, entry.getKey(), value.getAsString());
        }
        return blockState;
    }

    private static BlockState applyProperty(BlockState blockState, String name, String value) {
        Property<?> property = blockState.getBlock().getStateDefinition().getProperty(name);
        if (property == null) {
            Constants.LOG.warn("Block {} has no block state property '{}', ignoring", blockState.getBlock(), name);
            return blockState;
        }
        return applyPropertyValue(blockState, property, value);
    }

    private static BlockState applyPropertyValue(BlockState blockState, Property property, String value) {
        Optional parsed = property.getValue(value);
        if (parsed.isEmpty()) {
            Constants.LOG.warn("Invalid value '{}' for block state property '{}' (allowed: {}), ignoring", value, property.getName(), property.getPossibleValues());
            return blockState;
        }
        return blockState.setValue(property, (Comparable) parsed.get());
    }

    @Nullable
    private static JsonObject parseInlineState(String text) {
        JsonObject properties = new JsonObject();
        for (String pair : text.split(",")) {
            int split = pair.indexOf('=');
            if (split <= 0) {
                if (!pair.isBlank()) {
                    Constants.LOG.warn("Ignoring malformed block state entry '{}'", pair.trim());
                }
                continue;
            }
            properties.addProperty(pair.substring(0, split).trim(), pair.substring(split + 1).trim());
        }
        return properties.isEmpty() ? null : properties;
    }
}
