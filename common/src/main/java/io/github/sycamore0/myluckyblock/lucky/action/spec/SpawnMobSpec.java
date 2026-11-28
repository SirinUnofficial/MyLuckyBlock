package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpawnMobSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    @SerializedName("velocity")
    protected Vector3 velocity = new Vector3(0.0, 0.0, 0.0);

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

    @SerializedName("passengers")
    protected List<EntityNode> passengers = null;

    public String getId() {
        return id;
    }

    public PositionSpec getPos() {
        return pos;
    }

    public Vec3 getVelocity() {
        return new Vec3(velocity.getX(), velocity.getY(), velocity.getZ());
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

    public boolean isNameVisible() {
        return nameVisible;
    }

    public boolean isBaby() {
        return isBaby;
    }

    public boolean isUseRandom() {
        return useRandom;
    }

    public RandomNum getRandomNum() {
        return randomNum;
    }

    public int getNum() {
        return num;
    }

    public String getNbt() {
        return nbt;
    }

    public List<EntityNode> getPassengers() {
        return passengers == null ? List.of() : passengers;
    }
}
