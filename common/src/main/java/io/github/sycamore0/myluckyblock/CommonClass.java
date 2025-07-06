package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.platform.Services;
import io.github.sycamore0.myluckyblock.utils.helper.VersionHelper;

import java.util.ArrayList;
import java.util.List;

public class CommonClass {
    public static List<JsonObject> getLoadedEvents(String eventPackId) {
        return Constants.loadedEventPacks.getOrDefault(eventPackId, new ArrayList<>());
    }

    public static void init() {
        addEventPackId(Constants.MOD_ID);
    }

    public static void addEventPackId(String eventPackId) {
        if (!Constants.eventPackIdList.contains(eventPackId)) {
            Constants.eventPackIdList.add(eventPackId);
        }
    }

    public static boolean checkModLoaded(String modId) {
        return Services.PLATFORM.isModLoaded(modId);
    }
}
