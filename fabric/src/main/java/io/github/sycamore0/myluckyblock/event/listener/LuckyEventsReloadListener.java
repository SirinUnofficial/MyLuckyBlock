package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import io.github.sycamore0.myluckyblock.utils.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.utils.reader.EventPackDataReader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.*;

public class LuckyEventsReloadListener implements SimpleSynchronousResourceReloadListener, ILuckyEventsReloadListener {
    private volatile Map<String, List<EventPackDataReader>> PACK_DATA = Collections.emptyMap();
    private static final String EVENTS_PATH = "lucky/events";

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_events_loader");
    }

    public Map<String, List<EventPackDataReader>> getPackData() {
        return PACK_DATA;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        PACK_DATA = getAllEvents(manager);
        BreakLuckyBlock.MANAGER = new LuckyEventDataManager(this);
    }

    private Map<String, List<EventPackDataReader>> getAllEvents(ResourceManager manager) {
        Map<String, List<EventPackDataReader>> eventsMap = new HashMap<>();
        manager.listResources(EVENTS_PATH, path -> path.getPath().endsWith(".json"))
                .forEach((id, res) -> parseEvent(id, res, eventsMap));

        return eventsMap;
    }

    private void parseEvent(ResourceLocation id, Resource resource, Map<String, List<EventPackDataReader>> map) {
        String[] seg = id.getPath().split("/");
        if (seg.length < 3) return;
        String packId = seg[2];

        try (InputStreamReader reader = new InputStreamReader(resource.open())) {
            EventPackDataReader event = Constants.GSON.fromJson(reader, EventPackDataReader.class);
            map.computeIfAbsent(packId, k -> new ArrayList<>()).add(event);
        } catch (Exception exception) {
            Constants.LOG.error("Failed to parse {}: {}", id, exception.getMessage());
        }
    }
}