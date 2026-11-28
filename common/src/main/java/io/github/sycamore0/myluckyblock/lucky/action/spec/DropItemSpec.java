package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class DropItemSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("use_random")
    protected boolean useRandom = false;

    @SerializedName("random_num")
    protected RandomNum randomNum = new RandomNum(0, 1);

    @SerializedName("num")
    protected int num = 1;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

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

    public PositionSpec getPos() {
        return pos == null ? PositionSpec.DEFAULT : pos;
    }

    public boolean isUseRandom() {
        return useRandom;
    }

    public RandomNum getRandomNum() {
        return randomNum == null ? RandomNum.DEFAULT : randomNum;
    }

    public int getNum() {
        return num;
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

}
