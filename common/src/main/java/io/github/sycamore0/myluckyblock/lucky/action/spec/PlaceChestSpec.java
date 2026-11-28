package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class PlaceChestSpec {
    @SerializedName("id")
    protected String id = "empty";

    @SerializedName("seed")
    protected long seed = 0L;

    @SerializedName("chest_id")
    protected String chestId = "minecraft:chest";

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    public String getId() {
        return id;
    }

    public PositionSpec getPos() {
        return pos;
    }

    public String getChestId() {
        return chestId;
    }

    public long getSeed() {
        return seed;
    }
}
