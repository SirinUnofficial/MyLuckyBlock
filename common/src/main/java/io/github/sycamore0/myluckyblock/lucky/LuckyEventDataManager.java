package io.github.sycamore0.myluckyblock.lucky;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.listener.ILuckyEventsReloadListener;
import io.github.sycamore0.myluckyblock.platform.Services;
import io.github.sycamore0.myluckyblock.utils.helper.VersionHelper;
import io.github.sycamore0.myluckyblock.lucky.reader.DependenciesDataReader;
import io.github.sycamore0.myluckyblock.lucky.reader.EventPackDataReader;
import io.github.sycamore0.myluckyblock.lucky.reader.RandomEventDataReader;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyEventDataManager {
    private final ILuckyEventsReloadListener listener;
    private final Map<String, Map<String, LoadedPack>> packsByGroup = new ConcurrentHashMap<>();

    public LuckyEventDataManager(ILuckyEventsReloadListener listener) {
        this.listener = listener;
        listener.setOnReload(packsByGroup::clear);
    }

    public void loadEvents(String group, boolean includeBuiltIn) {
        Map<String, List<EventPackDataReader>> all = listener.getPackData();

        List<EventPackDataReader> rawPacks = new ArrayList<>(all.getOrDefault(group, List.of()));
        if (includeBuiltIn && !group.equals(Constants.EVENT_PACK_GROUP_NAME)) {
            rawPacks.addAll(all.getOrDefault(Constants.EVENT_PACK_GROUP_NAME, List.of()));
        }

        Map<String, LoadedPack> loaded = new LinkedHashMap<>();
        for (EventPackDataReader packData : rawPacks) {
            if (!checkDependencies(packData)) {
                packData.getDependencies().forEach(dep -> Constants.LOG.warn("Skipping pack {}: missing dependency {} {}", packData.getName(), dep.getModId(), dep.getVersionRange()));
                continue;
            }
            LoadedPack loadedPack = LoadedPack.from(packData);
            if (loaded.putIfAbsent(loadedPack.eventPackId(), loadedPack) != null) {
                Constants.LOG.warn("Duplicate pack eventPackId {} in group {}, keeping first", loadedPack.eventPackId(), group);
            }
        }

        packsByGroup.put(group, Collections.unmodifiableMap(loaded));

        int totalEvents = loaded.values().stream().mapToInt(LoadedPack::eventCount).sum();
        Constants.LOG.info("Loaded group {}: {} packs, {} events", group, loaded.size(), totalEvents);
    }

    public void ensureAllGroupsLoaded() {
        for (String group : listener.getPackData().keySet()) {
            if (!isLoaded(group)) {
                loadEvents(group, false);
            }
        }
    }

    public boolean isLoaded(String group) {
        return packsByGroup.containsKey(group);
    }

    @Nullable
    public RandomEventDataReader getRandomEvent(String group) {
        List<RandomEventDataReader> all = collect(group, null);
        if (all.isEmpty()) return null;
        return all.get(ThreadLocalRandom.current().nextInt(all.size()));
    }

    @Nullable
    public RandomEventDataReader getRandomEvent(String group, EventType type) {
        List<RandomEventDataReader> list = collect(group, type);
        if (list.isEmpty() && type != EventType.COMMON) {
            list = collect(group, EventType.COMMON);
        }
        if (list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    @Nullable
    public RandomEventDataReader getEvent(String group, String packId, int eventId) {
        LoadedPack pack = getPack(group, packId);
        return pack == null ? null : pack.eventsById().get(eventId);
    }

    @Nullable
    public LoadedPack getPack(String group, String packId) {
        Map<String, LoadedPack> packs = packsByGroup.get(group);
        return packs == null ? null : packs.get(packId);
    }

    public List<LoadedPack> getAllLoadedPacks() {
        return packsByGroup.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue))
                .toList();
    }

    public Set<String> getEventPackIds(String eventPackGroup) {
        Map<String, LoadedPack> loadedPacks = packsByGroup.get(eventPackGroup);
        return loadedPacks == null ? Set.of() : loadedPacks.keySet();
    }

    public Set<Integer> getEventIds(String group, String packId) {
        LoadedPack pack = getPack(group, packId);
        return pack == null ? Set.of() : pack.eventsById().keySet();
    }

    private static String groupOf(String fullEventPackId) {
        int colon = fullEventPackId.indexOf(':');
        return colon < 0 ? fullEventPackId : fullEventPackId.substring(0, colon);
    }

    @Nullable
    public RandomEventDataReader getEventByFullId(String fullEventPackId, int eventId) {
        return getEvent(groupOf(fullEventPackId), fullEventPackId, eventId);
    }

    public Set<Integer> getEventIdsByFullId(String fullEventPackId) {
        return getEventIds(groupOf(fullEventPackId), fullEventPackId);
    }

    public Set<String> getAllPackIds() {
        Set<String> allPackIds = new LinkedHashSet<>();
        for (Map<String, LoadedPack> loadedPacks : packsByGroup.values()) {
            allPackIds.addAll(loadedPacks.keySet());
        }
        return allPackIds;
    }

    private List<RandomEventDataReader> collect(String group, @Nullable EventType type) {
        Map<String, LoadedPack> packs = packsByGroup.get(group);
        if (packs == null) return List.of();
        List<RandomEventDataReader> result = new ArrayList<>();
        for (LoadedPack pack : packs.values()) {
            result.addAll(type == null
                    ? pack.allEvents()
                    : pack.eventsByType().getOrDefault(type, List.of()));
        }
        return result;
    }

    private boolean checkDependencies(EventPackDataReader packData) {
        List<DependenciesDataReader> needed = packData.getDependencies();
        if (needed == null) return true;
        for (DependenciesDataReader dep : needed) {
            String modId = dep.getModId();
            if (modId == null) continue;
            if (!Services.PLATFORM.isModLoaded(modId)) return false;

            String range = dep.getVersionRange();
            if (range == null) continue;

            String currentVersion;
            try {
                currentVersion = modId.equals("minecraft")
                        ? SharedConstants.getCurrentVersion().getName()
                        : Services.PLATFORM.getModVersion(modId);
            } catch (Exception e) {
                Constants.LOG.error("Failed to get version for {}", modId, e);
                return false;
            }

            if (currentVersion == null || !VersionHelper.isVersionInRange(currentVersion, range)) {
                return false;
            }
        }
        return true;
    }

    public record LoadedPack(
            String eventPackId,
            EventPackDataReader eventPackData,
            Map<Integer, RandomEventDataReader> eventsById,
            Map<EventType, List<RandomEventDataReader>> eventsByType,
            List<RandomEventDataReader> allEvents
    ) {
        static LoadedPack from(EventPackDataReader eventPackData) {
            String eventPackId = eventPackData.getEventPackId() != null ? eventPackData.getEventPackId() : eventPackData.getName();
            List<RandomEventDataReader> rawEvents = eventPackData.getRandomEvents();

            Set<Integer> explicitIds = new HashSet<>();
            for (RandomEventDataReader e : rawEvents) {
                if (e.getEventId() > 0) explicitIds.add(e.getEventId());
            }

            Map<Integer, RandomEventDataReader> byId = new LinkedHashMap<>();
            Map<EventType, List<RandomEventDataReader>> byType = new EnumMap<>(EventType.class);
            for (EventType t : EventType.values()) byType.put(t, new ArrayList<>());

            int nextAutoId = 1;
            for (RandomEventDataReader event : rawEvents) {
                int id = event.getEventId();
                if (id <= 0) {
                    while (explicitIds.contains(nextAutoId) || byId.containsKey(nextAutoId)) {
                        nextAutoId++;
                    }
                    id = nextAutoId++;
                    event.setEventId(id);
                }
                if (byId.containsKey(id)) {
                    Constants.LOG.warn("Duplicate event eventPackId {} in pack {}, skipping later one", id, eventPackId);
                    continue;
                }
                EventType type = event.getType() == null ? EventType.COMMON : event.getType();
                byId.put(id, event);
                byType.get(type).add(event);
            }

            byType.replaceAll((k, v) -> List.copyOf(v));

            return new LoadedPack(
                    eventPackId,
                    eventPackData,
                    Collections.unmodifiableMap(byId),
                    Collections.unmodifiableMap(byType),
                    List.copyOf(byId.values())
            );
        }

        public int eventCount() {
            return allEvents.size();
        }

        public int eventCountOf(EventType type) {
            return eventsByType().getOrDefault(type, List.of()).size();
        }
    }
}