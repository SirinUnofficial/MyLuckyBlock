package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import io.github.sycamore0.myluckyblock.world.gen.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyLuckyBlock implements ModInitializer {
    public static final String MOD_ID = "myluckyblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Map<String, List<JsonObject>> loadedEventsByMod = new HashMap<>();
    public static final List<String> modIdList = new ArrayList<>();

    public static List<JsonObject> getLoadedEventsForMod(String modId) {
        return loadedEventsByMod.getOrDefault(modId, new ArrayList<>());
    }

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModItemGroups.registerModItemGroups();
        ModEventHandlers.onInitialize();
        ModWorldGen.register();

        addModId(MOD_ID);

        // Data Loader
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of(MOD_ID, "lucky_events_loader");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        loadedEventsByMod.clear();
                        // Load events for all mods
                        for (String modId : modIdList) {
                            loadEventsForMod(manager, modId);
                        }
                        LOGGER.info("Loaded {} event files for mod {}", getLoadedEventsForMod(MOD_ID).size(), MOD_ID);
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
        manager.findResources(jsonDir, path -> path.getPath().endsWith(".json"))
                .forEach((id, resource) -> {
                    try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        json.addProperty("fileName", id.getPath());
                        events.add(json);
                    } catch (Exception e) {
                        LOGGER.error("Failed to load {}", id, e);
                    }
                });
        loadedEventsByMod.put(modId, events);
    }
}