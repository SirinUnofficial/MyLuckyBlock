package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;

import java.util.*;

public class LuckyDataManager {
    private final Map<String, List<LuckyEventReader>> eventsByMod = new HashMap<>();

    public void loadEvents(String modId, boolean includeBuiltIn) {

        // load events for the specified mod
        List<JsonObject> modEvents = MyLuckyBlock.getLoadedEventsForMod(modId);
        List<JsonObject> targetEvents = new ArrayList<>(modEvents);

        // if include built-in events
        if (includeBuiltIn && !modId.equals(Constants.MOD_ID)) {
            List<JsonObject> mainEvents = MyLuckyBlock.getLoadedEventsForMod(Constants.MOD_ID);
            targetEvents.addAll(mainEvents);
        }

        // Parse and store events
        List<LuckyEventReader> modEventList = new ArrayList<>();
        int currentId = 1;
        for (JsonObject json : targetEvents) {
            try {
                LuckyDataReader data = LuckyJsonUtil.loadJsonData(json);
                if (data == null) {
                    Constants.LOG.error("Failed to parse JSON file: {}", json.get("fileName").getAsString());
                    continue;
                }

                Constants.LOG.info("Loading {} (v{})", data.getName(), data.getVersion());

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
            } catch (Exception e) {
                Constants.LOG.error("Critical error loading {}: {}",
                        json.get("fileName").getAsString(),
                        e.getMessage()
                );
            }
        }

        eventsByMod.put(modId, modEventList);
        Constants.LOG.info("Successfully loaded {} random events for mod {}", modEventList.size(), modId);
    }

    public boolean isLoaded(String modId) {
        return eventsByMod.containsKey(modId);
    }

    public LuckyEventReader getRandomEvent(String modId) {
        List<LuckyEventReader> events = eventsByMod.get(modId);
        if (events == null || events.isEmpty()) {
            return null;
        }
        return events.get(new Random().nextInt(events.size()));
    }

    public int getRandomEventsCount(String modId) {
        return eventsByMod.getOrDefault(modId, new ArrayList<>()).size();
    }

    public LuckyEventReader getEventById(String modId, int id) {
        for (LuckyEventReader event : eventsByMod.getOrDefault(modId, new ArrayList<>())) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }
}