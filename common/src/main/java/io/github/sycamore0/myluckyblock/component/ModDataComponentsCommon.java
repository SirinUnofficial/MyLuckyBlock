package io.github.sycamore0.myluckyblock.component;

import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import net.minecraft.core.component.DataComponentType;

import java.util.Objects;
import java.util.function.Supplier;

public final class ModDataComponentsCommon {
    private static Supplier<DataComponentType<LuckyBlockData>> luckyBlockData;

    private ModDataComponentsCommon() {}

    public static void setLuckyBlockData(Supplier<DataComponentType<LuckyBlockData>> supplier) {
        luckyBlockData = supplier;
    }

    public static DataComponentType<LuckyBlockData> luckyBlockData() {
        return Objects.requireNonNull(luckyBlockData, "LuckyBlockData component not registered").get();
    }
}