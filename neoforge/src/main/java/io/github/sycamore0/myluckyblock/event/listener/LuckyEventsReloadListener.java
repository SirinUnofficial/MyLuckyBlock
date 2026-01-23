package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.reader.EventPackDataReader;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LuckyEventsReloadListener implements PreparableReloadListener, ILuckyEventsReloadListener {
    private volatile Map<String, List<EventPackDataReader>> PACK_DATA = Map.of();
    private static final String EVENTS_PATH = "lucky/events";

    @Override
    public @NotNull String getName() {
        return Constants.MOD_ID + ":lucky_events_loader";
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller prepProfilerFiller, @NotNull ProfilerFiller reloadProfilerFiller, @NotNull Executor bgExecutor, @NotNull Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            prepProfilerFiller.startTick();
            Map<String, List<EventPackDataReader>> eventsMap = new HashMap<>();

            resourceManager.listResources(EVENTS_PATH, loc -> loc.getPath().endsWith(".json"))
                    .forEach((location, resource) -> {
                        String path = location.getPath(); // lucky/events/<eventPackId>/*.json
                        String[] seg = path.split("/");
                        if (seg.length < 3) return;
                        String eventPackId = seg[2];

                        try (Reader reader = resource.openAsReader()) {
                            EventPackDataReader event = Constants.GSON.fromJson(reader, EventPackDataReader.class);
                            eventsMap.computeIfAbsent(eventPackId, k -> new ArrayList<>()).add(event);
                        } catch (Exception e) {
                            Constants.LOG.error("Failed to parse {}: {}", location, e.getMessage());
                        }
                    });
            prepProfilerFiller.endTick();
            return eventsMap;
        }, bgExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(res -> PACK_DATA = res, gameExecutor);
    }

    public Map<String, List<EventPackDataReader>> getPackData() {
        return PACK_DATA;
    }
}