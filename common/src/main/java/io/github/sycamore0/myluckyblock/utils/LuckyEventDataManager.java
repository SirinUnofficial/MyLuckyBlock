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

public class LuckyEventDataManager {
    private final ILuckyEventsReloadListener LISTENER;
    private final Map<String, List<RandomEventDataReader>> EVENTS_BY_GROUP = new ConcurrentHashMap<>();

    public LuckyEventDataManager(ILuckyEventsReloadListener listener) {
        this.LISTENER = listener;
    }

    public void loadEvents(String eventPackGroupName, boolean includeBuiltIn) {
        Map<String, List<EventPackDataReader>> all = LISTENER.getPackData();
        List<EventPackDataReader> eventPackList = new ArrayList<>(all.getOrDefault(eventPackGroupName, List.of()));

        if (includeBuiltIn && !eventPackGroupName.equals(Constants.EVENT_PACK_GROUP_NAME)) {
            eventPackList.addAll(all.getOrDefault(Constants.EVENT_PACK_GROUP_NAME, List.of()));
        }

        List<RandomEventDataReader> eventDataList = new ArrayList<>();
        int eventId = 1;
        for (EventPackDataReader packData : eventPackList) {
            if (!checkDependencies(packData)) {
                packData.getDependencies().forEach(dependency -> {
                    Constants.LOG.warn("Skipping event pack {}: missing dependency {} {}", packData.getName(), dependency.getModId(), dependency.getVersionRange());
                });
                continue;
            }
            for (RandomEventDataReader eventData : packData.getRandomEvents()) {
                eventData.setId(eventId++);
                eventDataList.add(eventData);
            }
        }
        EVENTS_BY_GROUP.put(eventPackGroupName, eventDataList);
        Constants.LOG.info("Loaded {} random events from event pack group {}", eventDataList.size(), eventPackGroupName);
    }

    public boolean isLoaded(String eventPackGroupName) {
        return EVENTS_BY_GROUP.containsKey(eventPackGroupName);
    }

    public RandomEventDataReader getRandomEvent(String eventPackGroupName) {
        List<RandomEventDataReader> list = EVENTS_BY_GROUP.get(eventPackGroupName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(new Random().nextInt(list.size()));
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