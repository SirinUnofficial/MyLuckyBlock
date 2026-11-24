package io.github.sycamore0.myluckyblock.lucky;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.event.listener.ILuckyEventsReloadListener;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.platform.Services;
import io.github.sycamore0.myluckyblock.utils.helper.VersionHelper;
import io.github.sycamore0.myluckyblock.lucky.reader.DependenciesDataReader;
import io.github.sycamore0.myluckyblock.lucky.reader.EventPack;
import io.github.sycamore0.myluckyblock.lucky.reader.EventDefinition;
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
        Map<String, List<EventPack>> all = listener.getPacksByGroup();
        if (all == null) {
            Constants.LOG.warn("Event pack data is not loaded yet, cannot load group {}", group);
            return;
        }

        List<EventPack> rawPacks = new ArrayList<>(all.getOrDefault(group, List.of()));
        if (includeBuiltIn && !group.equals(Constants.EVENT_PACK_GROUP_NAME)) {
            rawPacks.addAll(all.getOrDefault(Constants.EVENT_PACK_GROUP_NAME, List.of()));
        }

        Map<String, LoadedPack> loaded = new LinkedHashMap<>();
        int versionSkips = 0;
        for (EventPack eventPack : rawPacks) {
            DependenciesDataReader unsatisfied = findUnsatisfiedDependency(eventPack);
            if (unsatisfied != null) {
                if (isOtherVersionSkip(unsatisfied)) {
                    versionSkips++;
                    Constants.LOG.debug("Skipping pack {}: it needs minecraft {}", eventPack.getName(), unsatisfied.getVersionRange());
                } else {
                    Constants.LOG.warn("Skipping pack {}: missing dependency {}", eventPack.getName(), describeDependency(unsatisfied));
                }
                continue;
            }
            LoadedPack loadedPack = LoadedPack.from(eventPack);
            validateSteps(eventPack.getName(), loadedPack);
            if (loaded.putIfAbsent(loadedPack.packKey(), loadedPack) != null) {
                Constants.LOG.warn("Duplicate pack '{}' in group {}, keeping first", loadedPack.packKey(), group);
            }
        }

        packsByGroup.put(group, Collections.unmodifiableMap(loaded));

        int totalEvents = loaded.values().stream().mapToInt(LoadedPack::eventCount).sum();
        if (versionSkips > 0) {
            Constants.LOG.info("Loaded group {}: {} packs, {} events ({} skipped for other Minecraft versions)",
                    group, loaded.size(), totalEvents, versionSkips);
        } else {
            Constants.LOG.info("Loaded group {}: {} packs, {} events", group, loaded.size(), totalEvents);
        }
    }

    private static void validateSteps(String packName, LoadedPack pack) {
        for (EventDefinition event : pack.allEvents()) {
            List<JsonObject> steps = event.getSteps();
            for (int i = 0; i < steps.size(); i++) {
                JsonObject step = steps.get(i);
                if (step == null) continue;

                JsonElement actionElement = step.get("action");
                if (actionElement == null || !actionElement.isJsonPrimitive()) {
                    Constants.LOG.warn("Pack {}: event {} step #{} has no 'action'", packName, event.getEventId(), i + 1);
                    continue;
                }
                String actionType = actionElement.getAsString();
                LuckyAction luckyAction = LuckyActions.get(actionType);
                if (luckyAction == null) {
                    Constants.LOG.warn("Pack {}: event {} step #{} uses unknown action '{}'", packName, event.getEventId(), i + 1, actionType);
                    continue;
                }
                try {
                    luckyAction.validate(step);
                } catch (RuntimeException e) {
                    Constants.LOG.warn("Pack {}: event {} step #{} ('{}') has an invalid spec: {}", packName, event.getEventId(), i + 1, actionType, e.getMessage());
                }
            }
        }
    }

    public void ensureAllGroupsLoaded() {
        Map<String, List<EventPack>> all = listener.getPacksByGroup();
        if (all == null) {
            return;
        }
        for (String group : all.keySet()) {
            if (!isLoaded(group)) {
                loadEvents(group, false);
            }
        }
    }

    public boolean isLoaded(String group) {
        return packsByGroup.containsKey(group);
    }

    @Nullable
    public EventDefinition getRandomEvent(String group) {
        List<EventDefinition> all = collect(group, null);
        if (all.isEmpty()) return null;
        return all.get(ThreadLocalRandom.current().nextInt(all.size()));
    }

    @Nullable
    public EventDefinition getRandomEvent(String group, EventType type) {
        List<EventDefinition> list = collect(group, type);
        if (list.isEmpty() && type != EventType.COMMON) {
            list = collect(group, EventType.COMMON);
        }
        if (list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    @Nullable
    public EventDefinition getEvent(String group, String packKey, int eventId) {
        LoadedPack pack = getEventPack(group, packKey);
        return pack == null ? null : pack.eventsById().get(eventId);
    }

    @Nullable
    public LoadedPack getEventPack(String group, String packKey) {
        Map<String, LoadedPack> packs = packsByGroup.get(group);
        return packs == null ? null : packs.get(packKey);
    }

    public List<LoadedPack> getAllLoadedPacks() {
        return packsByGroup.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue))
                .toList();
    }

    public Set<String> getPackKeys(String eventPackGroup) {
        Map<String, LoadedPack> loadedPacks = packsByGroup.get(eventPackGroup);
        return loadedPacks == null ? Set.of() : loadedPacks.keySet();
    }

    public Set<Integer> getEventIds(String group, String packKey) {
        LoadedPack pack = getEventPack(group, packKey);
        return pack == null ? Set.of() : pack.eventsById().keySet();
    }

    private static String groupOf(String packKey) {
        int colon = packKey.indexOf(':');
        return colon < 0 ? packKey : packKey.substring(0, colon);
    }

    @Nullable
    public EventDefinition getEventByFullId(String packKey, int eventId) {
        return getEvent(groupOf(packKey), packKey, eventId);
    }

    public Set<Integer> getEventIdsByFullId(String packKey) {
        return getEventIds(groupOf(packKey), packKey);
    }

    public Set<String> getAllPackKeys() {
        Set<String> allPackKeys = new LinkedHashSet<>();
        for (Map<String, LoadedPack> loadedPacks : packsByGroup.values()) {
            allPackKeys.addAll(loadedPacks.keySet());
        }
        return allPackKeys;
    }

    private List<EventDefinition> collect(String group, @Nullable EventType type) {
        Map<String, LoadedPack> packs = packsByGroup.get(group);
        if (packs == null) return List.of();
        List<EventDefinition> result = new ArrayList<>();
        for (LoadedPack pack : packs.values()) {
            result.addAll(type == null
                    ? pack.allEvents()
                    : pack.eventsByType().getOrDefault(type, List.of()));
        }
        return result;
    }

    @Nullable
    private static DependenciesDataReader findUnsatisfiedDependency(EventPack eventPack) {
        List<DependenciesDataReader> needed = eventPack.getDependencies();
        if (needed == null) return null;
        for (DependenciesDataReader dep : needed) {
            String modId = dep.getModId();
            if (modId == null) continue;
            if (!Services.PLATFORM.isModLoaded(modId)) return dep;

            String range = dep.getVersionRange();
            if (range == null) continue;

            String currentVersion;
            try {
                currentVersion = modId.equals("minecraft")
                        ? SharedConstants.getCurrentVersion().getName()
                        : Services.PLATFORM.getModVersion(modId);
            } catch (Exception e) {
                Constants.LOG.error("Failed to get version for {}", modId, e);
                return dep;
            }

            if (currentVersion == null || !VersionHelper.isVersionInRange(currentVersion, range)) {
                return dep;
            }
        }
        return null;
    }

    private static boolean isOtherVersionSkip(DependenciesDataReader dep) {
        String modId = dep.getModId();
        if (modId == null || !"minecraft".equals(modId) || dep.getVersionRange() == null) {
            return false;
        }
        if (!ConfigManager.get().silenceMinecraftVersionSkipWarnings) {
            return false;
        }
        return Services.PLATFORM.isModLoaded(modId);
    }

    private static String describeDependency(DependenciesDataReader dep) {
        String range = dep.getVersionRange();
        return range == null ? dep.getModId() : dep.getModId() + " " + range;
    }

    public boolean isDataReady() {
        return listener.getPacksByGroup() != null;
    }

    public record LoadedPack(
            String packKey,
            EventPack eventPack,
            Map<Integer, EventDefinition> eventsById,
            Map<EventType, List<EventDefinition>> eventsByType,
            List<EventDefinition> allEvents
    ) {
        static LoadedPack from(EventPack eventPack) {
            String packKey = eventPack.getPackKey() != null ? eventPack.getPackKey() : eventPack.getName();
            List<EventDefinition> rawEvents = eventPack.getRandomEvents();

            Set<Integer> explicitIds = new HashSet<>();
            for (EventDefinition e : rawEvents) {
                if (e.getEventId() > 0) explicitIds.add(e.getEventId());
            }

            Map<Integer, EventDefinition> byId = new LinkedHashMap<>();
            Map<EventType, List<EventDefinition>> byType = new EnumMap<>(EventType.class);
            for (EventType t : EventType.values()) byType.put(t, new ArrayList<>());

            int nextAutoId = 1;
            for (EventDefinition event : rawEvents) {
                int id = event.getEventId();
                if (id <= 0) {
                    while (explicitIds.contains(nextAutoId) || byId.containsKey(nextAutoId)) {
                        nextAutoId++;
                    }
                    id = nextAutoId++;
                    event.setEventId(id);
                }
                if (byId.containsKey(id)) {
                    Constants.LOG.warn("Duplicate event id {} in pack '{}', skipping later one", id, packKey);
                    continue;
                }
                EventType type = event.getType() == null ? EventType.COMMON : event.getType();
                byId.put(id, event);
                byType.get(type).add(event);
            }

            byType.replaceAll((k, v) -> List.copyOf(v));

            return new LoadedPack(
                    packKey,
                    eventPack,
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