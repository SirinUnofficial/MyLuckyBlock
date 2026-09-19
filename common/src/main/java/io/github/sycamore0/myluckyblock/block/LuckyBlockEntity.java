package io.github.sycamore0.myluckyblock.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class LuckyBlockEntity extends BlockEntity {
    @Nullable
    private static Supplier<? extends BlockEntityType<?>> typeSupplier;

    public static void setTypeSupplier(@Nullable Supplier<? extends BlockEntityType<?>> supplier) {
        typeSupplier = supplier;
    }

    private int luckyValue = 0;
    private boolean limitedOutcomes = false;
    private List<String> outcomes = List.of();

    public LuckyBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Objects.requireNonNull(typeSupplier, "LuckyBlockEntity type supplier not initialized").get(), blockPos, blockState);
    }

    public int getLuckyValue() {
        return luckyValue;
    }

    public boolean isLimitedOutcomes() {
        return limitedOutcomes;
    }

    public List<String> getOutcomes() {
        return outcomes;
    }

    public void applyData(LuckyBlockData luckyBlockData) {
        this.luckyValue = Mth.clamp(luckyBlockData.luckyValue(), LuckyBlockData.MIN_LUCKY_VALUE, LuckyBlockData.MAX_LUCKY_VALUE);
        this.limitedOutcomes = luckyBlockData.isLimitedOutcomes();
        this.outcomes = luckyBlockData.outcomes() == null ? List.of() : List.copyOf(luckyBlockData.outcomes());
        setChanged();
    }

    public LuckyBlockData toData() {
        return new LuckyBlockData(luckyValue, limitedOutcomes, outcomes);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(nbt, registries);
        LuckyBlockData luckyBlockData = toData();
        if (luckyBlockData.isEmpty()) return;
        nbt.merge(luckyBlockData.toTag());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(nbt, registries);
        LuckyBlockData luckyBlockData = LuckyBlockData.fromTag(nbt);
        this.luckyValue = luckyBlockData.luckyValue();
        this.limitedOutcomes = luckyBlockData.isLimitedOutcomes();
        this.outcomes = luckyBlockData.outcomes();
    }
}