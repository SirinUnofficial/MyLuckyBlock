package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.listener.ILuckyEventsReloadListener;
import io.github.sycamore0.myluckyblock.platform.Services;
import io.github.sycamore0.myluckyblock.utils.helper.VersionHelper;
import io.github.sycamore0.myluckyblock.utils.reader.DependenciesDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.EventDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.RandomEventReader;
import net.minecraft.SharedConstants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LuckyEventDataManager {
    private final ILuckyEventsReloadListener listener;
    private final Map<String, List<RandomEventReader>> eventsByMod = new ConcurrentHashMap<>();

    public LuckyEventDataManager(ILuckyEventsReloadListener listener) {
        this.listener = listener;
    }

    public void loadEvents(String eventPackId, boolean includeBuiltIn) {
        Map<String, List<EventDataReader>> all = listener.getData();
        List<EventDataReader> target = new ArrayList<>(all.getOrDefault(eventPackId, List.of()));

        if (includeBuiltIn && !eventPackId.equals(Constants.MOD_ID)) {
            target.addAll(all.getOrDefault(Constants.MOD_ID, List.of()));
        }

        List<RandomEventReader> list = new ArrayList<>();
        int id = 1;
        for (EventDataReader data : target) {
            if (!checkDependencies(data)) {
                continue;
            }
            for (RandomEventReader event : data.getRandomEvents()) {
                event.setId(id++);
                list.add(event);
            }
        }
        eventsByMod.put(eventPackId, list);
        Constants.LOG.info("Loaded {} random events for pack {}", list.size(), eventPackId);
    }

    public boolean isLoaded(String eventPackId) {
        return eventsByMod.containsKey(eventPackId);
    }

    public RandomEventReader getRandomEvent(String eventPackId) {
        List<RandomEventReader> list = eventsByMod.get(eventPackId);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(new Random().nextInt(list.size()));
    }

    private boolean checkDependencies(EventDataReader data) {
        List<DependenciesDataReader> dependencies = data.getDependencies();
        if (dependencies == null) {
            return true;
        }
        for (DependenciesDataReader dependency : dependencies) {
            String versionRange = dependency.getVersionRange();
            if (dependency.getModId() == null) {
                continue;
            }
            if (!Services.PLATFORM.isModLoaded(dependency.getModId())) {
                return false;
            }
            if (versionRange == null) {
                continue;
            }
            String dependencyVersion;
            try {
                if (dependency.getModId().equals("minecraft")) {
                    dependencyVersion = SharedConstants.getCurrentVersion().getName();
                } else {
                    dependencyVersion = Services.PLATFORM.getModVersion(dependency.getModId());
                }
            } catch (Exception e) {
                Constants.LOG.error("Failed to get version for {}", dependency.getModId(), e);
                return false;
            }
            if (dependencyVersion == null || !VersionHelper.isVersionInRange(dependencyVersion, versionRange)) {
                return false;
            }
        }
        return true;
    }
}