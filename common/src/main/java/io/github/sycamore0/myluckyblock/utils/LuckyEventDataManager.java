package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;

import java.util.*;

public class LuckyEventDataManager {
    private final Map<String, List<LuckyEventReader>> eventsByMod = new HashMap<>();

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
        List<LuckyEventReader> modEventList = new ArrayList<>();
        int currentId = 1;
        for (JsonObject json : targetEvents) {
            try {
                LuckyEventDataReader data = LuckyJsonUtil.loadJsonData(json);
                if (data == null) {
                    Constants.LOG.error("Failed to parse JSON file: {}", json.get("fileName").getAsString());
                    continue;
                }

                Constants.LOG.info("Loading {} (v{})", data.getName(), data.getVersion());

                boolean allDependenciesLoaded = true;
                List<DependenciesDataReader> dependencies = data.getDependencies();
                if (dependencies != null) {
                    for (DependenciesDataReader dependency : dependencies) {
                        if (!CommonClass.checkModLoaded(dependency.getId())) {
                            allDependenciesLoaded = false;
                            Constants.LOG.warn("Dependency {} is not loaded", dependency.getId());
                        }
                    }
                }

                if (allDependenciesLoaded) {
                    for (LuckyEventReader event : data.getRandomEvents()) {
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

    public LuckyEventReader getRandomEvent(String eventPackId) {
        List<LuckyEventReader> events = eventsByMod.get(eventPackId);
        if (events == null || events.isEmpty()) {
            return null;
        }
        return events.get(new Random().nextInt(events.size()));
    }

    // Debug method
    public int getRandomEventsCount(String eventPackId) {
        return eventsByMod.getOrDefault(eventPackId, new ArrayList<>()).size();
    }

    // Debug method
    public LuckyEventReader getEventById(String eventPackId, int id) {
        for (LuckyEventReader event : eventsByMod.getOrDefault(eventPackId, new ArrayList<>())) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }
}