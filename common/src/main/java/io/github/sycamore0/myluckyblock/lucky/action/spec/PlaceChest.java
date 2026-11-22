package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class PlaceChest {
    @SerializedName("id")
    protected String id = "empty";

    @SerializedName("seed")
    protected long seed = 0L;

    @SerializedName("chest_id")
    protected String chestId = "minecraft:chest";

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    public PosSrc getPosSrc() {
        return PosSrc.fromValue(posSrc);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
    }

    public String getId() {
        return id;
    }

    public String getChestId() {
        return chestId;
    }

    public long getSeed() {
        return seed;
    }
}
