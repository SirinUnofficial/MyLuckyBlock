package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class CreateExplosionSpec {
    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    @SerializedName("power")
    protected int power = 1;

    @SerializedName("create_fire")
    protected boolean createFire = false;

    public int getPower() {
        return power;
    }

    public boolean isCreateFire() {
        return createFire;
    }

    public PositionSpec getPos() {
        return pos;
    }
}
