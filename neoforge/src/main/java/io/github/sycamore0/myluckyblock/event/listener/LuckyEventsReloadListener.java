package io.github.sycamore0.myluckyblock.event.listener;

import com.google.gson.Gson;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.reader.EventDataReader;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LuckyEventsReloadListener implements PreparableReloadListener, ILuckyEventsReloadListener {
    private static final Gson GSON = new Gson();
    private volatile Map<String, List<EventDataReader>> data = Map.of();

    @Override
    public @NotNull String getName() {
        return Constants.MOD_ID + ":lucky_events_loader";
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller prepProfilerFiller, @NotNull ProfilerFiller reloadProfilerFiller, @NotNull Executor bgExecutor, @NotNull Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            prepProfilerFiller.startTick();
            Map<String, List<EventDataReader>> map = new HashMap<>();

            resourceManager.listResources("lucky/events", loc -> loc.getPath().endsWith(".json"))
                    .forEach((location, resource) -> {
                        String path = location.getPath(); // lucky/events/<eventPackId>/*.json
                        String[] seg = path.split("/");
                        if (seg.length < 3) return;
                        String eventPackId = seg[2];

                        try (Reader reader = resource.openAsReader()) {
                            EventDataReader event = GSON.fromJson(reader, EventDataReader.class);
                            map.computeIfAbsent(eventPackId, k -> new ArrayList<>()).add(event);
                        } catch (Exception e) {
                            Constants.LOG.error("Failed to parse {}: {}", location, e.getMessage());
                        }
                    });
            prepProfilerFiller.endTick();
            return map;
        }, bgExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(res -> data = res, gameExecutor);
    }

    public Map<String, List<EventDataReader>> getData() {
        return data;
    }
}