package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Mth;

public class RandomNum {
    @SerializedName("min")
    protected int min;

    @SerializedName("max")
    protected int max;

    public RandomNum(int min, int max) {
        int actualMin = Math.min(min, max);
        int actualMax = Math.max(min, max);
        this.min = Mth.clamp(actualMin, 0, 64);
        this.max = Mth.clamp(actualMax, this.min, 64);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
