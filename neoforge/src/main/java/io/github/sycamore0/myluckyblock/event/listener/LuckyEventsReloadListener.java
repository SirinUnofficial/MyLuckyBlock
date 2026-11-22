package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.EventType;
import io.github.sycamore0.myluckyblock.lucky.reader.DisabledDataReader;
import io.github.sycamore0.myluckyblock.lucky.reader.EventPack;
import io.github.sycamore0.myluckyblock.lucky.reader.EventDefinition;
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
    private volatile Map<String, List<EventPack>> packsByGroup = Map.of();
    private volatile Runnable onReload = () -> {};

    @Override
    public @NotNull String getName() {
        return Constants.DATA_LOADER_ID.toString();
    }

    @Override
    public void setOnReload(Runnable callback) {
        this.onReload = callback != null ? callback : () -> {};
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller prepProfilerFiller, @NotNull ProfilerFiller reloadProfilerFiller, @NotNull Executor bgExecutor, @NotNull Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            prepProfilerFiller.startTick();

            Set<String> disabled = loadDisabled(resourceManager);
            Map<String, List<EventPack>> eventsMap = loadEvents(resourceManager, disabled);

            prepProfilerFiller.endTick();
            return eventsMap;
        }, bgExecutor).thenCompose(preparationBarrier::wait).thenAcceptAsync(res -> {
            packsByGroup = res;
            onReload.run();
        }, gameExecutor);
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
                Constants.LOG.error("Failed to parse disabled event pack list from {}: {}", resource.sourcePackId(), e.getMessage());
            }
        }

        Constants.LOG.info("Disabled event packs: {}", disabledPackSet);
        return disabledPackSet;
    }

    private Map<String, List<EventPack>> loadEvents(ResourceManager manager, Set<String> disabledSet) {
        Map<String, List<EventPack>> eventPacksMap = new HashMap<>();
        Constants.LOG.info("Loading events with disabled: {}", disabledSet);

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

                    String packName = seg[seg.length - 1].replace(".json", "");
                    String subDir = seg.length >= 5 ? seg[3] : "";
                    String packKey = eventPackGroup + ":" + (subDir.isEmpty() ? "" : subDir + "/") + packName;

                    if (disabledSet.contains(packKey)) {
                        Constants.LOG.info("Skipping disabled event pack: {}", packKey);
                        return;
                    }

                    try (Reader reader = resource.openAsReader()) {
                        EventPack eventPack = Constants.GSON.fromJson(reader, EventPack.class);
                        eventPack.setPackKey(packKey);
                        if (eventPack.getRandomEvents() != null) {
                            for (EventDefinition event : eventPack.getRandomEvents()) {
                                event.setType(type);
                            }
                        }
                        eventPacksMap.computeIfAbsent(eventPackGroup, k -> new ArrayList<>()).add(eventPack);
                        Constants.LOG.info("Parsed pack: {} with type {}", packKey, type);
                    } catch (Exception e) {
                        Constants.LOG.error("Failed to parse event pack {}: {}", location, e.getMessage());
                    }
                });

        Constants.LOG.info("Parsed event groups: {}", eventPacksMap.keySet());
        return eventPacksMap;
    }

    public Map<String, List<EventPack>> getPacksByGroup() {
        return packsByGroup;
    }
}