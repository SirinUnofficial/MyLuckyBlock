package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public class PlaySoundSpec {
    private static final String DEFAULT_TARGET = "player";
    private static final String DEFAULT_SOURCE = "blocks";

    @SerializedName("id")
    protected String id;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    @SerializedName("volume")
    protected float volume = 1.0f;

    @SerializedName("pitch")
    protected float pitch = 1.0f;

    @SerializedName("target")
    protected String target = DEFAULT_TARGET;

    @SerializedName("source")
    protected String source = DEFAULT_SOURCE;

    public String getId() {
        return id;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public PositionSpec getPos() {
        return pos == null ? PositionSpec.DEFAULT : pos;
    }

    public String getTarget() {
        return target == null ? DEFAULT_TARGET : target.trim().toLowerCase(Locale.ROOT);
    }

    public String getSource() {
        return source == null ? DEFAULT_SOURCE : source;
    }
}
