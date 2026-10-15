package io.github.sycamore0.myluckyblock.block;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public final class ModBlocksCommon {
    private static Supplier<Block> luckyBlock;

    private ModBlocksCommon() {}

    public static void setLuckyBlock(@NotNull Supplier<Block> supplier) {
        luckyBlock = Objects.requireNonNull(supplier, "supplier");
    }

    public static Block luckyBlock() {
        return Objects.requireNonNull(luckyBlock, "LuckyBlock not registered").get();
    }
}