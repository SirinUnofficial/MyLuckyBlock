package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.world.phys.Vec3;

public class SpawnMob {
    @SerializedName("id")
    protected String id;

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("velocity")
    protected Vec3 velocity = new Vec3(0.0, 0.0, 0.0);

    @SerializedName("randomize")
    protected boolean randomize = true;

    @SerializedName("name")
    protected String name = null;

    @SerializedName("name_visible")
    protected boolean nameVisible = false;

    @SerializedName("desc")
    protected String desc = null;

    @SerializedName("is_baby")
    protected boolean isBaby = false;

    @SerializedName("use_random")
    protected boolean useRandom = false;

    @SerializedName("random_num")
    protected RandomNum randomNum = new RandomNum(0, 1);

    @SerializedName("num")
    protected int num = 1;

    @SerializedName("nbt")
    protected String nbt = null;

    @SerializedName("vehicle_id")
    protected String vehicleId = null;

    @SerializedName("vehicle_nbt")
    protected String vehicleNbt = null;

    @SerializedName("spread")
    protected Vector3 spread = new Vector3(0.0, 0.0, 0.0);

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

    public boolean isUseRandom() {
        return useRandom;
    }

    public RandomNum getRandomNum() {
        return randomNum;
    }

    public boolean getRandomize() {
        return randomize;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Boolean isNameVisible() {
        return nameVisible;
    }

    public Boolean isBaby() {
        return isBaby;
    }

    public int getNum() {
        return num;
    }

    public String getNbt() {
        return nbt;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleNbt() {
        return vehicleNbt;
    }

    public Vector3 getSpread() {
        return spread;
    }
}
