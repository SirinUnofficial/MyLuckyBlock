package io.github.sycamore0.myluckyblock.utils.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DataComponentsHelper {
    public static void applyDataComponents(ItemStack itemStack, @Nullable JsonObject dataComponents, RegistryAccess registryAccess) {
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
