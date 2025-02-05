package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;

import java.util.*;

public class LuckyDataManager {
    private final Map<String, List<LuckyEventReader>> eventsByMod = new HashMap<>();

    public void loadEvents(String modId, boolean includeBuiltIn) {

        // load events for the specified mod
        List<JsonObject> modEvents = MyLuckyBlock.getLoadedEventsForMod(modId);
        List<JsonObject> targetEvents = new ArrayList<>(modEvents);

        // if include built-in events
        if (includeBuiltIn && !modId.equals(MyLuckyBlock.MOD_ID)) {
            List<JsonObject> mainEvents = MyLuckyBlock.getLoadedEventsForMod(MyLuckyBlock.MOD_ID);
            targetEvents.addAll(mainEvents);
        }

        // sort filename
        targetEvents.sort((a, b) -> {
            String aName = a.get("fileName").getAsString();
            String bName = b.get("fileName").getAsString();
            if (aName.equals("default.json")) return -1;
            if (bName.equals("default.json")) return 1;
            return aName.compareToIgnoreCase(bName);
        });

        // Parse and store events
        List<LuckyEventReader> modEventList = new ArrayList<>();
        int currentId = 1;
        for (JsonObject json : targetEvents) {
            try {
                LuckyDataReader data = LuckyJsonUtil.loadJsonData(json);
                if (data == null) {
                    MyLuckyBlock.LOGGER.error("Failed to parse JSON file: {}", json.get("fileName").getAsString());
                    continue;
                }

                MyLuckyBlock.LOGGER.info("Loading {} (v{})", data.getName(), data.getVersion());

                for (LuckyEventReader event : data.getRandomEvents()) {
                    event.setId(currentId++);
                    modEventList.add(event);

                    if (event.getId() <= 0) {
                        MyLuckyBlock.LOGGER.warn("Invalid event ID in {}: {}", json.get("fileName"), event.getId());
                    }
                }

                MyLuckyBlock.LOGGER.info("Loaded {} events from {}",
                        data.getRandomEvents().size(),
                        json.get("fileName").getAsString()
                );
            } catch (Exception e) {
                MyLuckyBlock.LOGGER.error("Critical error loading {}: {}",
                        json.get("fileName").getAsString(),
                        e.getMessage()
                );
            }
        }

        eventsByMod.put(modId, modEventList);
        MyLuckyBlock.LOGGER.info("Successfully loaded {} random events for mod {}", modEventList.size(), modId);
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