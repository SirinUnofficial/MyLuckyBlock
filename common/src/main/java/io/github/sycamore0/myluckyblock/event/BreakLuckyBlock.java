package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.Config;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.LuckyBlockEntity;
import io.github.sycamore0.myluckyblock.component.ModDataComponentsCommon;
import io.github.sycamore0.myluckyblock.lucky.EventType;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventExecutor;
import io.github.sycamore0.myluckyblock.lucky.reader.RandomEventDataReader;
import io.github.sycamore0.myluckyblock.utils.helper.EnchantmentsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BreakLuckyBlock {
    public static LuckyEventDataManager manager;

    public static void breakLuckyBlock(Level level, @Nullable Player player, BlockPos blockPos, BlockState blockState) {
        breakLuckyBlock(level, player, blockPos, blockState, null);
    }

    public static void breakLuckyBlock(Level level, @Nullable Player player, BlockPos blockPos, BlockState blockState, @Nullable LuckyBlockData prefetchedData) {
        if (!(level instanceof ServerLevel serverLevel && blockState.getBlock() instanceof LuckyBlock luckyBlock)) {
            return;
        }

        LuckyBlockData data = prefetchedData;
        if (data == null) {
            if (serverLevel.getBlockEntity(blockPos) instanceof LuckyBlockEntity be) {
                data = be.toData();
            } else {
                data = LuckyBlockData.DEFAULT;
            }
        }

        if (!serverLevel.getBlockState(blockPos).isAir()) {
            serverLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
        }

        if (!Config.ENABLE_CREATIVE_TRIGGER && player != null && player.isCreative()) {
            return;
        }

        if (player != null && EnchantmentsHelper.checkSilkTouch(player)) {
            dropSelf(serverLevel, blockPos, luckyBlock, data);
            return;
        }

        String eventPackGroupName = luckyBlock.getEventPackGroupName();
        boolean includeBuiltIn = luckyBlock.isIncludeBuiltIn();
        if (!manager.isLoaded(eventPackGroupName)) {
            manager.loadEvents(eventPackGroupName, includeBuiltIn);
        }

        RandomEventDataReader event = selectEvent(eventPackGroupName, data);
        if (event != null) {
            LuckyEventExecutor.executeLuckyFunction(serverLevel, player, blockPos, event);
        }
    }

    @Nullable
    private static RandomEventDataReader selectEvent(String group, LuckyBlockData luckyBlockData) {
        if (luckyBlockData.isLimitedOutcomes() && !luckyBlockData.outcomes().isEmpty()) {
            List<String> pool = new ArrayList<>(luckyBlockData.outcomes());
            while (!pool.isEmpty()) {
                String pick = pool.remove(ThreadLocalRandom.current().nextInt(pool.size()));
                RandomEventDataReader event = resolveOutcome(pick);
                if (event != null) return event;
            }
            Constants.LOG.warn("All limited outcomes invalid, fallback to random. group={}", group);
        }

        EventType chosenType = chooseTypeByLuckyValue(luckyBlockData.luckyValue());
        RandomEventDataReader event = manager.getRandomEvent(group, chosenType);
        if (event == null) {
            event = manager.getRandomEvent(group);
        }
        return event;
    }

    @Nullable
    private static RandomEventDataReader resolveOutcome(String entry) {
        if (entry == null) return null;
        int hashIdx = entry.indexOf('#');
        if (hashIdx <= 0 || hashIdx == entry.length() - 1) {
            Constants.LOG.warn("Invalid outcome entry (missing '#'): {}", entry);
            return null;
        }
        String packId = entry.substring(0, hashIdx).trim();
        int eventId;
        try {
            eventId = Integer.parseInt(entry.substring(hashIdx + 1).trim());
        } catch (NumberFormatException e) {
            Constants.LOG.warn("Invalid outcome entry (bad id): {}", entry);
            return null;
        }
        RandomEventDataReader event = manager.getEventByFullId(packId, eventId);
        if (event == null) {
            Constants.LOG.warn("Outcome not found: {}", entry);
        }
        return event;
    }

    private static EventType chooseTypeByLuckyValue(int luckyValue) {
        LuckyBlockData.EventProbabilities prob = LuckyBlockData.probabilitiesFor(luckyValue);
        double rand = ThreadLocalRandom.current().nextDouble(100.0);
        if (rand < prob.lucky()) return EventType.LUCKY;
        if (rand < prob.lucky() + prob.unlucky()) return EventType.UNLUCKY;
        return EventType.COMMON;
    }

    private static void dropSelf(ServerLevel serverLevel, BlockPos blockPos, LuckyBlock luckyBlock, LuckyBlockData luckyBlockData) {
        ItemStack stack = new ItemStack(luckyBlock);
        if (luckyBlockData != null && !luckyBlockData.isEmpty()) {
            stack.set(ModDataComponentsCommon.luckyBlockData(), luckyBlockData);
        }
        serverLevel.addFreshEntity(new ItemEntity(serverLevel, blockPos.getCenter().x, blockPos.getY(), blockPos.getCenter().z, stack));
    }
}