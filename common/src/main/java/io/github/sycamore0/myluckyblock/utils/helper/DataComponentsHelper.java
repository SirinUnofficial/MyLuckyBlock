package io.github.sycamore0.myluckyblock.utils.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
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
    public static void applyItemComponents(ItemStack itemStack, @Nullable JsonObject dataComponents, RegistryAccess registryAccess) {
        if (dataComponents == null || dataComponents.isEmpty()) {
            return;
        }
        try {
            String snbt = jsonObjectToSnbt(dataComponents);
            CompoundTag nbt = TagParser.parseTag(snbt);
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
            DataComponentPatch patch = DataComponentPatch.CODEC
                    .parse(registryOps, nbt)
                    .result()
                    .orElse(DataComponentPatch.EMPTY);
            if (!patch.equals(DataComponentPatch.EMPTY)) {
                itemStack.applyComponents(patch);
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to apply data components {}: {}", dataComponents, e.getMessage());
        }
    }

    public static void applyBlockEntityComponents(ServerLevel serverLevel, BlockEntity blockEntity, Block block, String blockId, JsonObject dataComponents) {
        Item item = block.asItem();
        if (item == Items.AIR) {
            Constants.LOG.warn("Block {} has no item form, ignoring 'data_components'", blockId);
            return;
        }

        ItemStack stack = new ItemStack(item);
        DataComponentsHelper.applyItemComponents(stack, dataComponents, serverLevel.registryAccess());

        CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            blockEntityData.loadInto(blockEntity, serverLevel.registryAccess());
        }

        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null && blockEntity instanceof Container container) {
            NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
            contents.copyInto(items);
            for (int i = 0; i < items.size(); i++) {
                container.setItem(i, items.get(i));
            }
        }
    }

    private static String jsonObjectToSnbt(JsonObject jsonObject) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(quoteSnbtString(entry.getKey()))
                    .append(':')
                    .append(jsonElementToSnbt(entry.getValue()));
        }
        sb.append('}');
        return sb.toString();
    }

    private static String jsonElementToSnbt(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "null";
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive prim = element.getAsJsonPrimitive();
            if (prim.isBoolean() || prim.isNumber()) {
                return prim.getAsString();
            }
            String s = prim.getAsString();
            if (s.isEmpty()) {
                return "\"\"";
            }
            if (s.startsWith("{") || s.startsWith("[")) {
                return s;
            }
            if (s.equals("true") || s.equals("false")) {
                return s;
            }
            if (s.matches("-?\\d+[bBsSlL]?") || s.matches("-?\\d+\\.\\d+[fFdD]?")) {
                return s;
            }
            return quoteSnbtString(s);
        }
        if (element.isJsonArray()) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (JsonElement e : element.getAsJsonArray()) {
                if (!first) sb.append(',');
                first = false;
                sb.append(jsonElementToSnbt(e));
            }
            sb.append(']');
            return sb.toString();
        }
        if (element.isJsonObject()) {
            return jsonObjectToSnbt(element.getAsJsonObject());
        }
        return "null";
    }

    private static String quoteSnbtString(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 2);
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' || c == '\\') {
                sb.append('\\');
            }
            sb.append(c);
        }
        sb.append('"');
        return sb.toString();
    }
}
