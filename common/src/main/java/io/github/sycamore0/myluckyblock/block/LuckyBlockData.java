package io.github.sycamore0.myluckyblock.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.sycamore0.myluckyblock.component.ModDataComponentsCommon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record LuckyBlockData(int luckyValue, boolean isLimitedOutcomes, List<String> outcomes) {
    public static final int MIN_LUCKY_VALUE = -100;
    public static final int MAX_LUCKY_VALUE = 100;

    private static final String NBT_LUCKY_VALUE = "lucky_value";
    private static final String NBT_IS_LIMITED_OUTCOMES = "is_limited_outcomes";
    private static final String NBT_OUTCOMES = "outcomes";

    public static final LuckyBlockData DEFAULT = new LuckyBlockData(0, false, List.of());

    public static final Codec<LuckyBlockData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.optionalFieldOf("lucky_value", 0).forGetter(LuckyBlockData::luckyValue),
            Codec.BOOL.optionalFieldOf("is_limited_outcomes", false).forGetter(LuckyBlockData::isLimitedOutcomes),
            Codec.STRING.listOf().optionalFieldOf("outcomes", List.of()).forGetter(LuckyBlockData::outcomes)
    ).apply(inst, LuckyBlockData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LuckyBlockData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public LuckyBlockData {
        luckyValue = Mth.clamp(luckyValue, MIN_LUCKY_VALUE, MAX_LUCKY_VALUE);
        outcomes = outcomes == null ? List.of() : List.copyOf(outcomes);
    }

    public static EventProbabilities probabilitiesFor(int luckyValue) {
        int value = Mth.clamp(luckyValue, MIN_LUCKY_VALUE, MAX_LUCKY_VALUE);
        int lucky;
        int unlucky;
        if (value >= 0) {
            lucky = Math.min(100, value + 1);
            unlucky = (value <= 50) ? 1 : 0;
        } else {
            unlucky = Math.min(100, -value + 1);
            lucky = (value >= -50) ? 1 : 0;
        }
        int common = 100 - lucky - unlucky;
        return new EventProbabilities(common, lucky, unlucky);
    }

    public static LuckyBlockData fromItemStack(ItemStack itemStack) {
        LuckyBlockData luckyBlockData = itemStack.get(ModDataComponentsCommon.luckyBlockData());
        return luckyBlockData != null ? luckyBlockData : DEFAULT;
    }

    public static LuckyBlockData fromTag(CompoundTag compoundTag) {
        if (compoundTag == null || compoundTag.isEmpty()) return DEFAULT;
        int luckyValue = compoundTag.getInt(NBT_LUCKY_VALUE);
        boolean isLimitedOutcomes = compoundTag.getBoolean(NBT_IS_LIMITED_OUTCOMES);
        List<String> parsedOutcomes = List.of();
        if (compoundTag.contains(NBT_OUTCOMES, Tag.TAG_LIST)) {
            ListTag nbtOutcomes = compoundTag.getList(NBT_OUTCOMES, Tag.TAG_STRING);
            List<String> tmp = new ArrayList<>(nbtOutcomes.size());
            for (int i = 0; i < nbtOutcomes.size(); i++) tmp.add(nbtOutcomes.getString(i));
            parsedOutcomes = List.copyOf(tmp);
        }
        return new LuckyBlockData(luckyValue, isLimitedOutcomes, parsedOutcomes);
    }

    public CompoundTag toTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt(NBT_LUCKY_VALUE, luckyValue);
        compoundTag.putBoolean(NBT_IS_LIMITED_OUTCOMES, isLimitedOutcomes);
        if (!outcomes.isEmpty()) {
            ListTag outcome = new ListTag();
            for (String o : outcomes) outcome.add(StringTag.valueOf(o));
            compoundTag.put(NBT_OUTCOMES, outcome);
        }
        return compoundTag;
    }

    public boolean isEmpty() {
        return luckyValue == 0 && !isLimitedOutcomes && outcomes.isEmpty();
    }

    public record EventProbabilities(int common, int lucky, int unlucky) {}
}