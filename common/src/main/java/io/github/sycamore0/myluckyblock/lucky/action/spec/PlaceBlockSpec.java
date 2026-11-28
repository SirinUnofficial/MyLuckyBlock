package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class PlaceBlockSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    @SerializedName("nbt")
    protected String nbt = null;

    @SerializedName("data_components")
    protected JsonObject dataComponents = null;

    @SerializedName("block_state")
    protected JsonElement blockState = null;

    public String getId() {
        return id;
    }

    public PositionSpec getPos() {
        return pos;
    }

    public String getNbt() {
        return nbt;
    }

    public JsonObject getDataComponents() {
        return dataComponents;
    }

    public JsonElement getBlockState() {
        return blockState;
    }
}
