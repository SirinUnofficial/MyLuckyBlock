package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class PlaySound {
    @SerializedName("id")
    protected String id;

    @SerializedName("volume")
    protected float volume = 1.0f;

    @SerializedName("pitch")
    protected float pitch = 1.0f;

    public String getId() {
        return id;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
