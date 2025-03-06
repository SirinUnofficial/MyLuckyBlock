package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.platform.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonClass {
    public static final Map<String, List<JsonObject>> loadedEventsByMod = new HashMap<>();
    public static final List<String> modIdList = new ArrayList<>();

    public static List<JsonObject> getLoadedEventsForMod(String modId) {
        return loadedEventsByMod.getOrDefault(modId, new ArrayList<>());
    }

    public static void init() {
        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello World!");
        }
    }

    public static void addModId(String modId) {
        if (!modIdList.contains(modId)) {
            modIdList.add(modId);
        }
    }
}
