package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DropItemSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("use_random")
    protected boolean useRandom = false;

    @SerializedName("random_num")
    protected RandomNum randomNum = new RandomNum(0, 1);

    @SerializedName("num")
    protected int num = 1;

    @SerializedName("pos_src")
    protected int posSrc = 0;

    @SerializedName("offset")
    protected Vector3 offset = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("spread")
    protected Vector3 spread = new Vector3(0.0, 0.0, 0.0);

    @SerializedName("name")
    protected String name = null;

    @SerializedName("name_visible")
    protected boolean nameVisible = false;

    @SerializedName("desc")
    protected String desc = null;

    @SerializedName("nbt")
    protected String nbt = null;

    @SerializedName("data_components")
    protected JsonObject dataComponents = null;

    public String getId() {
        return id;
    }

    public boolean isUseRandom() {
        return useRandom;
    }

    public RandomNum getRandomNum() {
        return randomNum;
    }

    public int getNum() {
        return Mth.clamp(num, 1, 64);
    }

    public PosSrc getPosSrc() {
        return PosSrc.fromValue(posSrc);
    }

    public Vec3 getOffset() {
        return new Vec3(offset.getX(), offset.getY(), offset.getZ());
    }

    public String getName() {
        return name;
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

    public String getDesc() {
        return desc;
    }

    public String getNbt() {
        return nbt;
    }

    public JsonObject getDataComponents() {
        return dataComponents;
    }

    public Vector3 getSpread() {
        return spread;
    }
}
