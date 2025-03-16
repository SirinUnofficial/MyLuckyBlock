package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.CommonClass;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

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