package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.world.phys.Vec3;

public class FallBlockSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    @SerializedName("velocity")
    protected Vec3 velocity = new Vec3(0.0, 0.0, 0.0);

    @SerializedName("block_state")
    protected JsonElement blockState = null;

    @SerializedName("nbt")
    protected String nbt = null;

    @SerializedName("data_components")
    protected JsonObject dataComponents = null;

    public Vec3 getVelocity() {
        return velocity == null ? Vec3.ZERO : velocity;
    }

    public String getId() {
        return id;
    }

    public PositionSpec getPos() {
        return pos == null ? PositionSpec.DEFAULT : pos;
    }

    public JsonElement getBlockState() {
        return blockState;
    }

    public String getNbt() {
        return nbt;
    }

    public JsonObject getDataComponents() {
        return dataComponents;
    }
}
