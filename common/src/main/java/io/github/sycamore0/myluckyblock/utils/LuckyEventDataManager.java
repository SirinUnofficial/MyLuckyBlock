package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.platform.Services;
import io.github.sycamore0.myluckyblock.utils.helper.VersionHelper;
import net.minecraft.SharedConstants;

import java.util.*;

public class LuckyEventDataManager {
    private final Map<String, List<RandomEventReader>> eventsByMod = new HashMap<>();

    public void loadEvents(String eventPackId, boolean includeBuiltIn) {
        // load events for the specified mod
        List<JsonObject> modEvents = CommonClass.getLoadedEvents(eventPackId);
        List<JsonObject> targetEvents = new ArrayList<>(modEvents);

        // if include built-in events
        if (includeBuiltIn && !eventPackId.equals(Constants.MOD_ID)) {
            List<JsonObject> mainEvents = CommonClass.getLoadedEvents(Constants.MOD_ID);
            targetEvents.addAll(mainEvents);
        }

        // Parse and store events
        List<RandomEventReader> modEventList = new ArrayList<>();
        int currentId = 1;
        for (JsonObject json : targetEvents) {
            try {
                EventDataReader data = ModJsonUtil.loadJsonData(json);
                if (data == null) {
                    Constants.LOG.error("Failed to parse JSON file: {}", json.get("fileName").getAsString());
                    continue;
                }

                Constants.LOG.info("Loading {} (v{})", data.getName(), data.getVersion());

                boolean allDependenciesLoaded = checkDependencies(data);

                if (allDependenciesLoaded) {
                    for (RandomEventReader event : data.getRandomEvents()) {
                        event.setId(currentId++);
                        modEventList.add(event);

                        if (event.getId() <= 0) {
                            Constants.LOG.warn("Invalid event ID in {}: {}", json.get("fileName"), event.getId());
                        }
                    }

                    Constants.LOG.info("Loaded {} events from {}",
                            data.getRandomEvents().size(),
                            json.get("fileName").getAsString()
                    );
                } else {
                    Constants.LOG.warn("Skipping {} because not all dependencies are loaded", json.get("fileName").getAsString());
                }
            } catch (Exception e) {
                Constants.LOG.error("Critical error loading {}: {}",
                        json.get("fileName").getAsString(),
                        e.getMessage()
                );
            }
        }

        eventsByMod.put(eventPackId, modEventList);
        Constants.LOG.info("Successfully loaded {} random events for mod {}", modEventList.size(), eventPackId);
    }

    public boolean isLoaded(String eventPackId) {
        return eventsByMod.containsKey(eventPackId);
    }

    public RandomEventReader getRandomEvent(String eventPackId) {
        List<RandomEventReader> events = eventsByMod.get(eventPackId);
        if (events == null || events.isEmpty()) {
            return null;
        }
        return events.get(new Random().nextInt(events.size()));
    }

    private boolean checkDependencies(EventDataReader data) {
        List<DependenciesDataReader> dependencies = data.getDependencies();

        if (dependencies == null) {
            return true;
        }

        for (DependenciesDataReader dependency : dependencies) {
            if (dependency.getModId() == null) {
                return true;
            }

            if (!CommonClass.checkModLoaded(dependency.getModId())) {
                Constants.LOG.warn("Dependency {} is not loaded", dependency.getModId());
                return false;
            } else {
                String versionRange = dependency.getVersionRange();
                if (versionRange == null) {
                    return true;
                }
                String currentDependencyVersion;
                try {
                    if (Objects.equals(dependency.getModId(), "minecraft")) {
                        currentDependencyVersion = SharedConstants.getCurrentVersion().getName();
                    }
                    else {
                        currentDependencyVersion = Services.PLATFORM.getModVersion(dependency.getModId());
                    }
                } catch (Exception e) {
                    Constants.LOG.error("Failed to get version for dependency {}", dependency.getModId(), e);
                    return false;
                }

                if (currentDependencyVersion == null) {
                    return false;
                }

                if (!VersionHelper.isVersionInRange(currentDependencyVersion, versionRange)) {
                    Constants.LOG.warn("Dependency {} version {} is not in range {}",
                            dependency.getModId(), currentDependencyVersion, versionRange);
                    return false;
                }
            }
        }

        return true;
    }
}