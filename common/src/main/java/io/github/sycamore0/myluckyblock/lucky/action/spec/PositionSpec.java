package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class PositionSpec {
    public static final PositionSpec DEFAULT = new PositionSpec();

    private static final Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("src")
    protected int src = 0;

    @SerializedName("offset")
    protected Vector3 offset = ZERO;

    @SerializedName("spread")
    protected Vector3 spread = ZERO;

    public PosSrc getSrc() {
        return PosSrc.fromValue(src);
    }

    public Vec3 getOffset() {
        Vector3 value = offset == null ? ZERO : offset;
        return new Vec3(value.getX(), value.getY(), value.getZ());
    }

    public Vector3 getSpread() {
        return spread == null ? ZERO : spread;
    }

    public boolean isDefault() {
        return src == 0 && isZero(offset) && isZero(spread);
    }

    private static boolean isZero(Vector3 value) {
        return value == null || value.getX() == 0.0 && value.getY() == 0.0 && value.getZ() == 0.0;
    }
}
