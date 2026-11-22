package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class AddParticle {
    @SerializedName("id")
    protected String id;

    @SerializedName("count")
    protected int count = 1;

    @SerializedName("delta")
    protected Vector3 delta = new Vector3(1.0, 1.0, 1.0);

    @SerializedName("speed")
    protected double speed = 0.0;

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public Vector3 getDelta() {
        return delta;
    }

    public double getSpeed() {
        return speed;
    }

    public PosSrc getPosSrc() {
        return PosSrc.fromValue(posSrc);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
    }
}
