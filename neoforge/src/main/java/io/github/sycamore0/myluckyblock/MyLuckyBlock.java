package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.event.ModEventHandlers;
import io.github.sycamore0.myluckyblock.item.ModItemGroups;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Mod(Constants.MOD_ID)
public class MyLuckyBlock {
    public MyLuckyBlock(IEventBus eventBus) {
        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        ModBlocks.registerModBlocks(eventBus);
        ModItemGroups.registerModItemGroups(eventBus);
        NeoForge.EVENT_BUS.register(ModEventHandlers.class);

        CommonClass.addModId(Constants.MOD_ID);
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
        CommonClass.loadedEventsByMod.put(modId, events);
    }
}
