package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public record LuckyWellSpec(
        @SerializedName("lucky_block") @Nullable LuckyBlockOverrides luckyBlock,
        @SerializedName("wall_block") @Nullable String wallBlock,
        @SerializedName("fence_block") @Nullable String fenceBlock,
        @SerializedName("lid_block") @Nullable String lidBlock,
        @SerializedName("glass_block") @Nullable String glassBlock,
        @SerializedName("plate_block") @Nullable String plateBlock,
        @SerializedName("water_block") @Nullable String waterBlock) {
    public static final LuckyWellSpec DEFAULT = new LuckyWellSpec(null, null, null, null, null, null, null);
}
