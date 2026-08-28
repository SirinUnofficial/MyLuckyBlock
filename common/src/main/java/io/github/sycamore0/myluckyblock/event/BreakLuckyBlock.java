package io.github.sycamore0.myluckyblock.event;

import io.github.sycamore0.myluckyblock.Config;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.utils.*;
import io.github.sycamore0.myluckyblock.utils.helper.*;
import io.github.sycamore0.myluckyblock.utils.reader.RandomEventDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BreakLuckyBlock {
    public static LuckyEventDataManager manager;

    public static void breakLuckyBlock(Level level, @Nullable Player player, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel && state.getBlock() instanceof LuckyBlock luckyBlock)) {
            return;
        }

        if (!Config.ENABLE_CREATIVE_TRIGGER) {
            if (player != null && player.isCreative()) {
                return;
            }
        }

        if (player != null && EnchantmentsHelper.checkSilkTouch(player)) {
            dropSelf(serverLevel, pos, luckyBlock);
            return;
        }

        String eventPackGroupName = luckyBlock.getEventPackGroupName();
        boolean includeBuiltIn = luckyBlock.isIncludeBuiltIn();

        if (!manager.isLoaded(eventPackGroupName)) {
            manager.loadEvents(eventPackGroupName, includeBuiltIn);
        }

        RandomEventDataReader event;
        if (Config.LUCK_ENABLED) {
            EventType chosenType = selectEventType(player);
            event = manager.getRandomEvent(eventPackGroupName, chosenType);
            if (event == null) {
                event = manager.getRandomEvent(eventPackGroupName, EventType.COMMON);
                if (event == null) {
                    event = manager.getRandomEvent(eventPackGroupName);
                }
            }
        } else {
            event = manager.getRandomEvent(eventPackGroupName);
        }

        if (event != null) {
            LuckyEventExecutor.executeLuckyFunction(serverLevel, player, pos, event);
        }
    }

    private static void dropSelf(ServerLevel level, BlockPos pos, LuckyBlock block) {
        level.addFreshEntity(new ItemEntity(level, pos.getCenter().x, pos.getY(), pos.getCenter().z, new ItemStack(block)));
    }

    private static EventType selectEventType(@Nullable Player player) {
        double luckProb = Config.LUCK_BASE;
        double unluckProb = Config.UNLUCK_BASE;

        if (player != null) {
            int luckLevel = getEffectLevel(player, MobEffects.LUCK);
            int unluckLevel = getEffectLevel(player, MobEffects.UNLUCK);

            luckProb += luckLevel * Config.LUCK_PER_LEVEL;
            unluckProb += unluckLevel * Config.UNLUCK_PER_LEVEL;
        }

        luckProb = Mth.clamp(luckProb, 0.0, 100.0);
        unluckProb = Mth.clamp(unluckProb, 0.0, 100.0);

        double total = luckProb + unluckProb;
        if (total > 100.0) {
            luckProb = (luckProb / total) * 100.0;
            unluckProb = (unluckProb / total) * 100.0;
        }

        double rand = ThreadLocalRandom.current().nextDouble(100.0);
        if (rand < luckProb) {
            return EventType.LUCKY;
        } else if (rand < luckProb + unluckProb) {
            return EventType.UNLUCKY;
        } else {
            return EventType.COMMON;
        }
    }

    private static int getEffectLevel(Player player, Holder<MobEffect> effect) {
        MobEffectInstance instance = player.getEffect(effect);
        return instance != null ? instance.getAmplifier() + 1 : 0;
    }
}