package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class GivePotionEffect {
    @SerializedName("id")
    protected String id;

    @SerializedName("amplifier")
    protected int amplifier = 0;

    @SerializedName("duration")
    protected int duration = 1;

    public String getId() {
        return id;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getDuration() {
        return duration;
    }
}
