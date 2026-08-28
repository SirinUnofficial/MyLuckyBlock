package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.listener.ILuckyEventsReloadListener;
import io.github.sycamore0.myluckyblock.platform.Services;
import io.github.sycamore0.myluckyblock.utils.helper.VersionHelper;
import io.github.sycamore0.myluckyblock.utils.reader.DependenciesDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.EventPackDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.RandomEventDataReader;
import net.minecraft.SharedConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyEventDataManager {
    private final ILuckyEventsReloadListener LISTENER;
    private final Map<String, List<RandomEventDataReader>> EVENTS_BY_GROUP = new ConcurrentHashMap<>();
    private final Map<String, Map<EventType, List<RandomEventDataReader>>> EVENTS_BY_GROUP_AND_TYPE = new ConcurrentHashMap<>();

    public LuckyEventDataManager(ILuckyEventsReloadListener listener) {
        this.LISTENER = listener;
    }

    public void loadEvents(String eventPackGroupName, boolean includeBuiltIn) {
        Map<String, List<EventPackDataReader>> all = LISTENER.getPackData();
        List<EventPackDataReader> eventPackList = new ArrayList<>(all.getOrDefault(eventPackGroupName, List.of()));

        if (includeBuiltIn && !eventPackGroupName.equals(Constants.EVENT_PACK_GROUP_NAME)) {
            eventPackList.addAll(all.getOrDefault(Constants.EVENT_PACK_GROUP_NAME, List.of()));
        }

        Map<EventType, List<RandomEventDataReader>> typeMap = new EnumMap<>(EventType.class);
        for (EventType type : EventType.values()) {
            typeMap.put(type, new ArrayList<>());
        }

        int eventId = 1;
        for (EventPackDataReader packData : eventPackList) {
            if (!checkDependencies(packData)) {
                packData.getDependencies().forEach(dep ->
                        Constants.LOG.warn("Skipping pack {}: missing dependency {} {}", packData.getName(), dep.getModId(), dep.getVersionRange()));
                continue;
            }
            for (RandomEventDataReader event : packData.getRandomEvents()) {
                event.setId(eventId++);
                EventType type = event.getType();
                if (type == null) type = EventType.COMMON;
                typeMap.get(type).add(event);
            }
        }

        EVENTS_BY_GROUP_AND_TYPE.put(eventPackGroupName, typeMap);

        List<RandomEventDataReader> allEvents = new ArrayList<>();
        typeMap.values().forEach(allEvents::addAll);
        EVENTS_BY_GROUP.put(eventPackGroupName, allEvents);

        Constants.LOG.info("Loaded {} events from group {} (lucky={}, unlucky={}, common={})",
                allEvents.size(), eventPackGroupName,
                typeMap.get(EventType.LUCKY).size(),
                typeMap.get(EventType.UNLUCKY).size(),
                typeMap.get(EventType.COMMON).size());
    }

    public boolean isLoaded(String eventPackGroupName) {
        return EVENTS_BY_GROUP_AND_TYPE.containsKey(eventPackGroupName);
    }

    public RandomEventDataReader getRandomEvent(String eventPackGroupName) {
        List<RandomEventDataReader> list = EVENTS_BY_GROUP.get(eventPackGroupName);
        if (list == null || list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public RandomEventDataReader getRandomEvent(String eventPackGroupName, EventType type) {
        Map<EventType, List<RandomEventDataReader>> typeMap = EVENTS_BY_GROUP_AND_TYPE.get(eventPackGroupName);
        if (typeMap == null) return null;
        List<RandomEventDataReader> list = typeMap.get(type);
        if (list == null || list.isEmpty()) {
            list = typeMap.get(EventType.COMMON);
        }
        if (list == null || list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private boolean checkDependencies(EventPackDataReader packData) {
        List<DependenciesDataReader> neededDependencies = packData.getDependencies();
        if (neededDependencies == null) {
            return true;
        }
        for (DependenciesDataReader neededDependency : neededDependencies) {
            String neededVersionRange = neededDependency.getVersionRange();
            if (neededDependency.getModId() == null) {
                continue;
            }
            if (!Services.PLATFORM.isModLoaded(neededDependency.getModId())) {
                return false;
            }
            if (neededVersionRange == null) {
                continue;
            }

            String currentDependencyVersion;
            try {
                if (neededDependency.getModId().equals("minecraft")) {
                    currentDependencyVersion = SharedConstants.getCurrentVersion().getName();
                } else {
                    currentDependencyVersion = Services.PLATFORM.getModVersion(neededDependency.getModId());
                }
            } catch (Exception exception) {
                Constants.LOG.error("Failed to get version for {}", neededDependency.getModId(), exception);
                return false;
            }

            if (currentDependencyVersion == null || !VersionHelper.isVersionInRange(currentDependencyVersion, neededVersionRange)) {
                return false;
            }
        }
        return true;
    }
}