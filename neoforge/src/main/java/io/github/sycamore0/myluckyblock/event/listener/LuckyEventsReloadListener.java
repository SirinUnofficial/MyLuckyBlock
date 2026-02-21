package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.reader.DisabledDataReader;
import io.github.sycamore0.myluckyblock.utils.reader.EventPackDataReader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LuckyEventsReloadListener implements PreparableReloadListener, ILuckyEventsReloadListener {
    private volatile Map<String, List<EventPackDataReader>> packData = Map.of();

    @Override
    public @NotNull String getName() {
        return Constants.DATA_LOADER_ID.toString();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller prepProfilerFiller, @NotNull ProfilerFiller reloadProfilerFiller, @NotNull Executor bgExecutor, @NotNull Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            prepProfilerFiller.startTick();

            Set<String> disabled = loadDisabled(resourceManager);
            Map<String, List<EventPackDataReader>> eventsMap = loadEvents(resourceManager, disabled);

            prepProfilerFiller.endTick();
            return eventsMap;
        }, bgExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(res -> packData = res, gameExecutor);
    }

    private Set<String> loadDisabled(ResourceManager manager) {
        Set<String> disabledPackSet = new HashSet<>();
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, Constants.DISABLED_DATA_PATH);

        List<Resource> resources = manager.getResourceStack(location);

        for (Resource resource : resources) {
            try (Reader reader = resource.openAsReader()) {
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
        Map<String, List<EventPackDataReader>> eventPacksMap = new HashMap<>();
        Constants.LOG.info("Loading events with disabled: {}", disabledSet);

        manager.listResources(Constants.EVENTS_PATH, loc -> loc.getPath().endsWith(".json"))
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

                    try (Reader reader = resource.openAsReader()) {
                        Constants.LOG.info("Loading event pack: {}", fullEventPackId);
                        EventPackDataReader eventPackData = Constants.GSON.fromJson(reader, EventPackDataReader.class);
                        eventPacksMap.computeIfAbsent(eventPackGroup, k -> new ArrayList<>()).add(eventPackData);
                    } catch (Exception e) {
                        Constants.LOG.error("Failed to parse {}: {}", location, e.getMessage());
                    }
                });

        Constants.LOG.info("Loaded event groups: {}", eventPacksMap.keySet());
        return eventPacksMap;
    }

    public Map<String, List<EventPackDataReader>> getPackData() {
        return packData;
    }
}