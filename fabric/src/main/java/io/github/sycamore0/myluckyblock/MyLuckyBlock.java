package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyLuckyBlock implements ModInitializer {
    public static final Map<String, List<JsonObject>> loadedEventsByMod = new HashMap<>();
    public static final List<String> modIdList = new ArrayList<>();

    public static List<JsonObject> getLoadedEventsForMod(String modId) {
        return loadedEventsByMod.getOrDefault(modId, new ArrayList<>());
    }

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModItemGroups.registerModItemGroups();
        ModEventHandlers.onInitialize();

        addModId(Constants.MOD_ID);

        // Data Loader
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_events_loader");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        loadedEventsByMod.clear();
                        // Load events for all mods
                        for (String modId : modIdList) {
                            loadEventsForMod(manager, modId);
                        }
                        Constants.LOG.info("Loaded {} event files for mod {}", getLoadedEventsForMod(Constants.MOD_ID).size(), Constants.MOD_ID);
                    }
                }
        );
    }

    public static void addModId(String modId) {
        if (!modIdList.contains(modId)) {
            modIdList.add(modId);
        }
    }

    public static void loadEventsForMod(ResourceManager manager, String modId) {
        String jsonDir = "lucky_events/" + modId;
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
        loadedEventsByMod.put(modId, events);
    }
}