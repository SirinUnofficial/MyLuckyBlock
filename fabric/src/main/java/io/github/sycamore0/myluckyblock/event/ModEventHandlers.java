package io.github.sycamore0.myluckyblock.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModEventHandlers {
    public static void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> BreakLuckyBlock.breakLuckyBlock(world, player, pos, state));

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_events_loader");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        Constants.loadedEventPacks.clear();
                        // Load events for all mods
                        for (String eventPackId : Constants.eventPackIdList) {
                            loadEventsPack(manager, eventPackId);
                        }
                        Constants.LOG.info("Loaded {} event files for mod {}", CommonClass.getLoadedEvents(Constants.MOD_ID).size(), Constants.MOD_ID);
                    }
                }
        );
    }

    private static void loadEventsPack(ResourceManager manager, String eventPackId) {
        String jsonDir = "lucky/events/" + eventPackId;
        List<JsonObject> events = new ArrayList<>();
        manager.listResources(jsonDir, path -> path.getPath().endsWith(".json"))
                .forEach((id, resource) -> {
                    try (InputStreamReader reader = new InputStreamReader(resource.open())) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        json.addProperty("fileName", id.getPath());
                        events.add(json);
                    } catch (Exception e) {
                        Constants.LOG.error("Failed to load {}", id, e);
                    }
                });
        Constants.loadedEventPacks.put(eventPackId, events);
    }
}