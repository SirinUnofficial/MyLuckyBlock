package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import io.github.sycamore0.myluckyblock.utils.EventType;
import io.github.sycamore0.myluckyblock.utils.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.utils.reader.DisabledDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.EventPackDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.RandomEventDataReader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class LuckyEventsReloadListener implements SimpleSynchronousResourceReloadListener, ILuckyEventsReloadListener {
    private volatile Map<String, List<EventPackDataReader>> packData = Collections.emptyMap();

    @Override
    public ResourceLocation getFabricId() {
        return Constants.DATA_LOADER_ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Set<String> disabled = loadDisabled(manager);
        packData = loadEvents(manager, disabled);
        BreakLuckyBlock.manager = new LuckyEventDataManager(this);
    }

    private Set<String> loadDisabled(ResourceManager manager) {
        Set<String> disabledPackSet = new HashSet<>();
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, Constants.DISABLED_DATA_PATH);

        List<Resource> resources = manager.getResourceStack(location);

        for (Resource resource : resources) {
            try (Reader reader = new InputStreamReader(resource.open())) {
                DisabledDataReader data = Constants.GSON.fromJson(reader, DisabledDataReader.class);
                if (data == null || data.getValues() == null) continue;

                if (data.isReplace()) {
                    disabledPackSet.clear();
                }
                disabledPackSet.addAll(data.getValues());

            } catch (Exception e) {
                Constants.LOG.error("Failed to parse disabled event pack list from {}: {}", resource.sourcePackId(), e.getMessage());
            }
        }

        Constants.LOG.info("Disabled event packs: {}", disabledPackSet);
        return disabledPackSet;
    }

    private Map<String, List<EventPackDataReader>> loadEvents(ResourceManager manager, Set<String> disabledSet) {
        Map<String, List<EventPackDataReader>> eventPacksMap = new HashMap<>();

        manager.listResources(Constants.EVENTS_PATH, loc -> loc.getPath().endsWith(".json"))
                .forEach((location, resource) -> {
                    String path = location.getPath();
                    String[] seg = path.split("/");
                    if (seg.length < 4) return;

                    String eventPackGroup = seg[2];
                    EventType type = EventType.COMMON;
                    if (seg.length >= 5) {
                        String subDir = seg[3];
                        if ("luck".equalsIgnoreCase(subDir)) {
                            type = EventType.LUCKY;
                        } else if ("unluck".equalsIgnoreCase(subDir)) {
                            type = EventType.UNLUCKY;
                        }
                    }

                    String eventPackId = seg[seg.length - 1].replace(".json", "");
                    String subDir = seg.length >= 5 ? seg[3] : "";
                    String fullEventPackId = eventPackGroup + ":" + (subDir.isEmpty() ? "" : subDir + "/") + eventPackId;

                    if (disabledSet.contains(fullEventPackId)) {
                        Constants.LOG.info("Skipping disabled event pack: {}", fullEventPackId);
                        return;
                    }

                    try (Reader reader = new InputStreamReader(resource.open())) {
                        EventPackDataReader eventPackData = Constants.GSON.fromJson(reader, EventPackDataReader.class);
                        if (eventPackData.getRandomEvents() != null) {
                            for (RandomEventDataReader event : eventPackData.getRandomEvents()) {
                                event.setType(type);
                            }
                        }
                        eventPacksMap.computeIfAbsent(eventPackGroup, k -> new ArrayList<>()).add(eventPackData);
                        Constants.LOG.info("Parsed pack: {} with type {}", fullEventPackId, type);
                    } catch (Exception e) {
                        Constants.LOG.error("Failed to parse event pack {}: {}", location, e.getMessage());
                    }
                });

        Constants.LOG.info("Parsed event groups: {}", eventPacksMap.keySet());
        return eventPacksMap;
    }

    public Map<String, List<EventPackDataReader>> getPackData() {
        return packData;
    }
}