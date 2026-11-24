package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class PlaceBlockSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("nbt")
    protected String nbt = null;

    @SerializedName("data_components")
    protected JsonObject dataComponents = null;

    @SerializedName("block_state")
    protected JsonElement blockState = null;

    public String getId() {
        return id;
    }

    public PosSrc getPosSrc() {
        return PosSrc.fromValue(posSrc);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
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
