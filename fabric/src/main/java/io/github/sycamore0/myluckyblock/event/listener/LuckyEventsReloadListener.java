package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import io.github.sycamore0.myluckyblock.utils.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.utils.reader.DisabledDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.EventPackDataReader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class LuckyEventsReloadListener implements SimpleSynchronousResourceReloadListener, ILuckyEventsReloadListener {
    private volatile Map<String, List<EventPackDataReader>> PACK_DATA = Collections.emptyMap();
    private static final String EVENTS_PATH = "lucky/events";
    private static final String DISABLED_DATA_PATH = "lucky/disabled.json";

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_events_loader");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Set<String> disabled = loadDisabled(manager);
        PACK_DATA = loadEvents(manager, disabled);
        BreakLuckyBlock.MANAGER = new LuckyEventDataManager(this);
    }

    private Set<String> loadDisabled(ResourceManager manager) {
        Set<String> disabledPackSet = new HashSet<>();
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, DISABLED_DATA_PATH);

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
                Constants.LOG.error("Failed to parse disabled data from {}: {}", resource.sourcePackId(), e.getMessage());
            }
        }

        Constants.LOG.info("Final disabled event packs: {}", disabledPackSet);
        return disabledPackSet;
    }

    private Map<String, List<EventPackDataReader>> loadEvents(ResourceManager manager, Set<String> disabledSet) {
        Map<String, List<EventPackDataReader>> eventsMap = new HashMap<>();
        Constants.LOG.info("Loading events with disabled: {}", disabledSet);

        manager.listResources(EVENTS_PATH, loc -> loc.getPath().endsWith(".json"))
                .forEach((location, resource) -> {
                    String path = location.getPath();
                    Constants.LOG.debug("Checking event pack file: {}", path);
                    String[] seg = path.split("/");
                    if (seg.length < 4) return;

                    String eventPackGroup = seg[2];
                    String eventPackId = seg[seg.length - 1].replace(".json", "");
                    String fullEventPackId = eventPackGroup + ":" + eventPackId;

                    if (disabledSet.contains(fullEventPackId)) {
                        Constants.LOG.info("Skipping disabled event pack: {}", fullEventPackId);
                        return;
                    }

                    try (Reader reader = new InputStreamReader(resource.open())) {
                        EventPackDataReader event = Constants.GSON.fromJson(reader, EventPackDataReader.class);
                        eventsMap.computeIfAbsent(eventPackGroup, k -> new ArrayList<>()).add(event);
                        Constants.LOG.info("Loaded event pack: {}", fullEventPackId);
                    } catch (Exception e) {
                        Constants.LOG.error("Failed to parse {}: {}", location, e.getMessage());
                    }
                });

        Constants.LOG.info("Loaded event groups: {}", eventsMap.keySet());
        return eventsMap;
    }

    public Map<String, List<EventPackDataReader>> getPackData() {
        return PACK_DATA;
    }
}