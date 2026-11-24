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

    public static void register(String actionType, LuckyAction luckyAction) {
        if (actionType == null || actionType.isBlank() || luckyAction == null) {
            Constants.LOG.warn("Ignoring invalid action registration: actionType={}, action={}", actionType, luckyAction);
            return;
        }
        LuckyAction previous = REGISTRY.putIfAbsent(actionType, luckyAction);
        if (previous != null) {
            Constants.LOG.warn("Action '{}' is already registered, keeping the existing one", actionType);
        }
    }

    public static void registerBuiltins() {
        BuiltinActions.register();
    }

    @Nullable
    public static LuckyAction get(String actionType) {
        return actionType == null ? null : REGISTRY.get(actionType);
    }

    public static boolean isRegistered(String actionType) {
        return actionType != null && REGISTRY.containsKey(actionType);
    }

    public static Set<String> actionTypes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(REGISTRY.keySet()));
    }

    public static <T> List<T> spec(JsonObject step, Class<T> elementType) {
        return spec(step, "spec", elementType);
    }

    public static <T> List<T> spec(JsonObject step, String key, Class<T> elementType) {
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
            Constants.LOG.warn("Step spec '{}' must be an array or an object, got {}", key, element);
            return List.of();
        }
        return parsed == null ? List.of() : parsed;
    }

    @Nullable
    public static <T> T specObject(JsonObject step, Class<T> specType) {
        if (step == null) {
            return null;
        }
        JsonElement element = step.get("spec");
        if (element == null || element.isJsonNull() || !element.isJsonObject()) {
            return null;
        }
        return Constants.GSON.fromJson(element, specType);
    }
}
