package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

// for lucky well
public record LuckyBlockOverrides(
        @SerializedName("id") @Nullable String id,
        @SerializedName("nbt") @Nullable String nbt,
        @SerializedName("data_components") @Nullable JsonObject dataComponents) {
    public static final LuckyBlockOverrides DEFAULT = new LuckyBlockOverrides(null, null, null);

    public boolean hasOverrides() {
        return (nbt != null && !nbt.isBlank()) || (dataComponents != null && !dataComponents.isEmpty());
    }
}
