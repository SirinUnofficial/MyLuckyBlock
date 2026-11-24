package io.github.sycamore0.myluckyblock.utils.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DataComponentsHelper {
    private static final String BLOCK_ENTITY_DATA = "minecraft:block_entity_data";

    public static void applyItemComponents(ItemStack itemStack, @Nullable JsonObject dataComponents, RegistryAccess registryAccess) {
        if (dataComponents == null || dataComponents.isEmpty()) {
            return;
        }

        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        for (Map.Entry<String, JsonElement> entry : dataComponents.entrySet()) {
            applyComponent(itemStack, entry.getKey(), entry.getValue(), ops);
        }
    }

    private static void applyComponent(ItemStack itemStack, String key, JsonElement value, RegistryOps<JsonElement> ops) {
        boolean remove = key.startsWith("!");
        String idString = remove ? key.substring(1) : key;
        ResourceLocation id = ResourceLocation.tryParse(idString);
        DataComponentType<?> type = id == null ? null : BuiltInRegistries.DATA_COMPONENT_TYPE.get(id);
        if (type == null) {
            Constants.LOG.warn("Unknown data component '{}', ignoring", key);
            return;
        }

        if (remove) {
            itemStack.remove((DataComponentType) type);
            return;
        }

        Codec codec = type.codecOrThrow();
        DataResult<?> result = codec.parse(ops, value);

        if (result.result().isEmpty()) {
            JsonElement legacy = stringifyComponents(value);
            if (!legacy.equals(value)) {
                DataResult<?> asLegacy = codec.parse(ops, legacy);
                if (asLegacy.result().isPresent()) {
                    result = asLegacy;
                }
            }
        }

        if (result.result().isEmpty()) {
            String error = result.error().map(DataResult.Error::message).orElse("unknown error");
            Constants.LOG.error("Failed to parse data component '{}' from {}: {}", key, value, error);
            return;
        }

        itemStack.set((DataComponentType) type, (Object) result.result().orElseThrow());
    }

    private static JsonElement stringifyComponents(JsonElement element) {
        if (element == null || element.isJsonNull() || element.isJsonPrimitive()) {
            return element;
        }
        if (element.isJsonArray()) {
            var array = new com.google.gson.JsonArray();
            for (JsonElement child : element.getAsJsonArray()) {
                array.add(stringifyComponents(child));
            }
            return array;
        }
        return new JsonPrimitive(element.toString());
    }

    public static void applyToBlockEntity(ServerLevel serverLevel, BlockEntity blockEntity, Block block, String blockId, JsonObject dataComponentsElement) {
        Item item = block.asItem();
        if (item == Items.AIR) {
            Constants.LOG.warn("Block {} has no item form, ignoring 'data_components'", blockId);
            return;
        }

        ItemStack itemStack = new ItemStack(item);
        applyItemComponents(itemStack, withBlockEntityId(dataComponentsElement, blockId), serverLevel.registryAccess());

        CustomData blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            blockEntityData.loadInto(blockEntity, serverLevel.registryAccess());
        }

        ItemContainerContents contents = itemStack.get(DataComponents.CONTAINER);
        if (contents != null && blockEntity instanceof Container container) {
            NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
            contents.copyInto(items);
            for (int i = 0; i < items.size(); i++) {
                container.setItem(i, items.get(i));
            }
        }
    }

    @Nullable
    public static CompoundTag toBlockEntityTag(RegistryAccess registryAccess, Item item, String blockId, @Nullable String nbtString, @Nullable JsonObject dataComponentsElement) {
        CompoundTag result = null;

        if (dataComponentsElement != null && !dataComponentsElement.isEmpty() && item != Items.AIR) {
            ItemStack itemStack = new ItemStack(item);
            applyItemComponents(itemStack, withBlockEntityId(dataComponentsElement, blockId), registryAccess);
            CustomData data = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (data != null && !data.isEmpty()) {
                result = data.copyTag();
            }
        }

        if (nbtString != null && !nbtString.isBlank()) {
            CompoundTag nbt;
            try {
                nbt = NbtHelper.generateNbt(nbtString);
            } catch (IllegalArgumentException e) {
                Constants.LOG.warn("Failed to parse 'nbt' for block {}: {}", blockId, e.getMessage());
                nbt = null;
            }
            if (nbt != null) {
                if (result == null) {
                    result = nbt;
                } else {
                    result.merge(nbt);
                }
            }
        }

        return result == null || result.isEmpty() ? null : result;
    }

    private static JsonObject withBlockEntityId(JsonObject dataComponentsElement, @Nullable String blockId) {
        JsonElement element = dataComponentsElement.get(BLOCK_ENTITY_DATA);
        if (element == null || !element.isJsonObject() || element.getAsJsonObject().has("id")) {
            return dataComponentsElement;
        }
        JsonObject copy = dataComponentsElement.deepCopy();
        copy.getAsJsonObject(BLOCK_ENTITY_DATA).addProperty("id", stripBlockState(blockId));
        return copy;
    }

    private static String stripBlockState(@Nullable String blockId) {
        if (blockId == null) {
            return "";
        }
        int bracket = blockId.indexOf('[');
        return (bracket < 0 ? blockId : blockId.substring(0, bracket)).trim();
    }
}
