package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class FallBlock {
    @SerializedName("id")
    protected String id;

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("velocity")
    protected Vec3 velocity = new Vec3(0.0, 0.0, 0.0);

    public PosSrc getPosSrc() {
        return PosSrc.fromValue(posSrc);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
    }

    public Vec3 getVelocity() {
        return velocity;
    }

    public String getId() {
        return id;
    }
}
