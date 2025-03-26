package io.github.sycamore0.myluckyblock.event;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModEventHandlers {
    @SubscribeEvent
    public static void onDestroyedByPlayer(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getState();

        if (event.getLevel() instanceof ServerLevel serverLevel) {
            // if lucky block
            if (blockState.getBlock() instanceof LuckyBlock) {
                serverLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());

                BreakLuckyBlock.breakLuckyBlock(
                        serverLevel,
                        player,
                        blockPos,
                        blockState
                );
                event.setCanceled(true);

                // cost durability
                ItemStack tool = player.getMainHandItem();
                if (tool.isDamageableItem()) {
                    int damage = blockState.getDestroySpeed(serverLevel, blockPos) > 0 ? 1 : 0;
                    tool.hurtAndBreak(damage, player, player.getEquipmentSlotForItem(tool));
                }
                player.awardStat(Stats.BLOCK_MINED.get(blockState.getBlock()));
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(ResourceLocation.parse("lucky_loader"), new LuckyEventsReloadListener());
    }

    private static class LuckyEventsReloadListener implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(@NotNull ResourceManager manager) {
            Constants.loadedEventPacks.clear();
            for (String eventPackId : Constants.eventPackIdList) {
                loadEventsPack(manager, eventPackId);
            }
            Constants.LOG.info("Loaded {} event files for mod {}",
                    CommonClass.getLoadedEvents(Constants.MOD_ID).size(),
                    Constants.MOD_ID);
        }
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