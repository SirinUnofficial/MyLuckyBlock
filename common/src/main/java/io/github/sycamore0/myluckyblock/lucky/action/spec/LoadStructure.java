package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class LoadStructure {
    @SerializedName("id")
    protected String id;

    @SerializedName("mod_id")
    protected String modId = Constants.MOD_ID;

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    public String getId() {
        return id;
    }

    public String getModId() {
        return modId;
    }

    public PosSrc getPosSrc() {
        return PosSrc.fromValue(posSrc);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
    }
}
