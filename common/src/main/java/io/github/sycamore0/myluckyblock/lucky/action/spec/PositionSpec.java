package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class PositionSpec {
    @SerializedName("src")
    protected int src = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("spread")
    protected Vector3 spread = new Vector3(0.0, 0.0, 0.0);

    public PosSrc getSrc() {
        return PosSrc.fromValue(src);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
    }

    public Vector3 getSpread() {
        return spread;
    }
}
