package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;

import java.util.*;

public class LuckyDataManager {
    protected final List<LuckyEventReader> randomEvents = new ArrayList<>();

    public LuckyDataManager() {
        loadEvents(MyLuckyBlock.getLoadedEvents()); // 直接使用已加载的数据
    }

    private void loadEvents(List<JsonObject> jsonObjects) {
        if (jsonObjects == null || jsonObjects.isEmpty()) {
            MyLuckyBlock.LOGGER.warn("No event data provided. Skipping event loading.");
            return;
        }

        // 优先加载 default.json，其他按文件名字母顺序排序
        jsonObjects.sort((a, b) -> {
            String aName = a.get("fileName").getAsString();
            String bName = b.get("fileName").getAsString();
            if (aName.equals("default.json")) return -1;
            if (bName.equals("default.json")) return 1;
            return aName.compareToIgnoreCase(bName);
        });

        int currentId = 1;
        for (JsonObject json : jsonObjects) {
            try {
                LuckyDataReader data = LuckyJsonUtil.loadJsonData(json);
                if (data == null) {
                    MyLuckyBlock.LOGGER.error("Failed to parse JSON file: {}", json.get("fileName").getAsString());
                    continue;
                }

                MyLuckyBlock.LOGGER.info("Loading {} (v{})", data.getName(), data.getVersion());

                for (LuckyEventReader event : data.getRandomEvents()) {
                    event.setId(currentId++);
                    randomEvents.add(event);

                    // 验证必要字段
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

        MyLuckyBlock.LOGGER.info("Successfully loaded {} random events", randomEvents.size());
    }

    public int getRandomEventsCount() {
        return randomEvents.size();
    }

    public LuckyEventReader getRandomEvent() {
        if (randomEvents.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(randomEvents.size());
        return randomEvents.get(randomIndex);
    }

    public LuckyEventReader getEventById(int id) {
        if (randomEvents.isEmpty()) {
            return null;
        }
        for (LuckyEventReader event : randomEvents) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }
}