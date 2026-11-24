package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.ActionSupport;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.PlaceBlock;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public final class PlaceBlocksAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (PlaceBlock block : LuckyActions.spec(step, PlaceBlock.class)) {
            placeBlock(context.level(), context.resolvePos(block.getPosSrc(), block.getOffset()), block.getId(), block.getState(), block.getNbt(), block.getDataComponents());
        }
    }

    private static void placeBlock(ServerLevel serverLevel, Vec3 pos, String blockId, @Nullable JsonObject state, @Nullable String nbtString, @Nullable JsonObject dataComponents) {
        BlockState blockState = resolveBlockState(blockId, state);
        if (blockState == null) {
            return;
        }
        BlockPos blockPos = PosHelper.parseVec3d(pos);
        serverLevel.setBlockAndUpdate(blockPos, blockState);
        ActionSupport.applyBlockEntityData(serverLevel, blockPos, blockId, nbtString, dataComponents);
    }

    @Nullable
    private static BlockState resolveBlockState(String blockId, @Nullable JsonObject state) {
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
            Constants.LOG.warn("Unknown block id '{}', skipping placement", blockId);
            return null;
        }

        BlockState blockState = block.defaultBlockState();
        blockState = applyState(blockState, inline);
        return applyState(blockState, state);
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
            Constants.LOG.warn("Block {} has no block state property '{}', ignoring",
                    blockState.getBlock(), name);
            return blockState;
        }
        return applyPropertyValue(blockState, property, value);
    }

    private static BlockState applyPropertyValue(BlockState blockState, Property property, String value) {
        Optional parsed = property.getValue(value);
        if (parsed.isEmpty()) {
            Constants.LOG.warn("Invalid value '{}' for block state property '{}' (allowed: {}), ignoring",
                    value, property.getName(), property.getPossibleValues());
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
