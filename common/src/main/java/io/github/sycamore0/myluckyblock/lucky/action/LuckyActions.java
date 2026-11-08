package io.github.sycamore0.myluckyblock.lucky.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.sycamore0.myluckyblock.Constants;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class LuckyActions {
    private static final Map<String, LuckyAction> REGISTRY = new ConcurrentHashMap<>();

    private LuckyActions() {
    }

    public static void register(String type, LuckyAction action) {
        if (type == null || type.isBlank() || action == null) {
            Constants.LOG.warn("Ignoring invalid action registration: type={}, action={}", type, action);
            return;
        }
        LuckyAction previous = REGISTRY.putIfAbsent(type, action);
        if (previous != null) {
            Constants.LOG.warn("Action type '{}' is already registered, keeping the existing one", type);
        }
    }

    public static void registerBuiltins() {
        BuiltinActions.register();
    }

    @Nullable
    public static LuckyAction get(String type) {
        return type == null ? null : REGISTRY.get(type);
    }

    public static boolean isRegistered(String type) {
        return type != null && REGISTRY.containsKey(type);
    }

    public static Set<String> types() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(REGISTRY.keySet()));
    }

    public static <T> List<T> payload(JsonObject step, Class<T> elementType) {
        return payload(step, "data", elementType);
    }

    public static <T> List<T> payload(JsonObject step, String key, Class<T> elementType) {
        if (step == null) return List.of();

        JsonElement element = step.get(key);
        if (element == null || element.isJsonNull()) return List.of();

        Type listType = TypeToken.getParameterized(List.class, elementType).getType();
        List<T> parsed;
        if (element.isJsonArray()) {
            parsed = Constants.GSON.fromJson(element, listType);
        } else if (element.isJsonObject()) {
            T single = Constants.GSON.fromJson(element, elementType);
            parsed = single == null ? List.of() : List.of(single);
        } else {
            Constants.LOG.warn("Action payload '{}' must be an array or an object, got {}", key, element);
            return List.of();
        }
        return parsed == null ? List.of() : parsed;
    }

    @Nullable
    public static <T> T payloadObject(JsonObject step, Class<T> type) {
        if (step == null) {
            return null;
        }
        JsonElement element = step.get("data");
        if (element == null || element.isJsonNull() || !element.isJsonObject()) {
            return null;
        }
        return Constants.GSON.fromJson(element, type);
    }
}
