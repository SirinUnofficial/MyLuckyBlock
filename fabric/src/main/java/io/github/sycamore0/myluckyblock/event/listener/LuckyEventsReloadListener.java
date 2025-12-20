package io.github.sycamore0.myluckyblock.event.listener;

import com.google.gson.Gson;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import io.github.sycamore0.myluckyblock.utils.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.utils.reader.EventDataReader;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.*;

public class LuckyEventsReloadListener implements SimpleSynchronousResourceReloadListener, ILuckyEventsReloadListener {
    private static final Gson GSON = new Gson();
    private volatile Map<String, List<EventDataReader>> data = Collections.emptyMap();

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_events_loader");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        data = scanAllEvents(manager);
        BreakLuckyBlock.manager = new LuckyEventDataManager(this);
    }

    public Map<String, List<EventDataReader>> getData() {
        return data;
    }

    private Map<String, List<EventDataReader>> scanAllEvents(ResourceManager resourceManager) {
        Map<String, List<EventDataReader>> map = new HashMap<>();
        resourceManager.listResources("lucky/events", path -> path.getPath().endsWith(".json"))
                .forEach((id, res) -> parseOneEvent(id, res, map));

        return map;
    }

    private void parseOneEvent(ResourceLocation location, Resource resource, Map<String, List<EventDataReader>> map) {
        String[] seg = location.getPath().split("/");
        if (seg.length < 3) return;
        String packId = seg[2];

        try (InputStreamReader reader = new InputStreamReader(resource.open())) {
            EventDataReader event = GSON.fromJson(reader, EventDataReader.class);
            map.computeIfAbsent(packId, k -> new ArrayList<>()).add(event);
        } catch (Exception e) {
            Constants.LOG.error("Failed to parse {}: {}", location, e.getMessage());
        }
    }
}