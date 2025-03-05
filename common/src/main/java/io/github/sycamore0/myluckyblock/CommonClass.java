package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.platform.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {
    public static final Map<String, List<JsonObject>> loadedEventsByMod = new HashMap<>();
    public static final List<String> modIdList = new ArrayList<>();

    public static List<JsonObject> getLoadedEventsForMod(String modId) {
        return loadedEventsByMod.getOrDefault(modId, new ArrayList<>());
    }

    public static void init() {
        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
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
