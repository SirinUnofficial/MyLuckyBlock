package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class ModEventHandlers {
    @SubscribeEvent
    public static void onDestroyedByPlayer(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            BreakLuckyBlock.breakLuckyBlock(
                    serverLevel,
                    event.getPlayer(),
                    event.getPos(),
                    event.getState()
            );
        }
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new LuckyEventsReloadListener());
    }

    private static class LuckyEventsReloadListener implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(@NotNull ResourceManager manager) {
            CommonClass.loadedEventsByMod.clear();
            for (String modId : CommonClass.modIdList) {
                MyLuckyBlock.loadEventsForMod(manager, modId);
            }
            Constants.LOG.info("Loaded {} event files for mod {}",
                    CommonClass.getLoadedEventsForMod(Constants.MOD_ID).size(),
                    Constants.MOD_ID);
        }
    }
}