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
import java.util.List;

public class MyLuckyBlock implements ModInitializer {
    public static final String MOD_ID = "myluckyblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String JSON_DIR = "lucky_events";
    private static final List<JsonObject> loadedEvents = new ArrayList<>();

    public static List<JsonObject> getLoadedEvents() {
        return loadedEvents;
    }

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModItemGroups.registerModItemGroups();
        ModEventHandlers.onInitialize();
        ModWorldGen.register();

        // Data Loader
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() { // 使用同步加载简化逻辑
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.of(MOD_ID, JSON_DIR);
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        loadedEvents.clear();
                        try {
                            manager.findResources(JSON_DIR, path -> path.getPath().endsWith(".json"))
                                    .forEach((id, resource) -> {
                                        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                                            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                                            json.addProperty("fileName", id.getPath());
                                            loadedEvents.add(json);
                                        } catch (Exception e) {
                                            LOGGER.error("Failed to load {}", id, e);
                                        }
                                    });
                            LOGGER.info("Loaded {} event files", loadedEvents.size());
                        } catch (Exception e) {
                            LOGGER.error("Resource reload failed", e);
                        }
                    }
                }
        );
    }
}