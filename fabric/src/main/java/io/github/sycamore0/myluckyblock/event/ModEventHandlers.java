package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

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
                        CommonClass.loadedEventsByMod.clear();
                        // Load events for all mods
                        for (String modId : CommonClass.modIdList) {
                            MyLuckyBlock.loadEventsForMod(manager, modId);
                        }
                        Constants.LOG.info("Loaded {} event files for mod {}", CommonClass.getLoadedEventsForMod(Constants.MOD_ID).size(), Constants.MOD_ID);
                    }
                }
        );
    }
}