package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class AddParticleSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("count")
    protected int count = 1;

    @SerializedName("delta")
    protected Vector3 delta = new Vector3(1.0, 1.0, 1.0);

    @SerializedName("speed")
    protected double speed = 0.0;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    public String getId() {
        return id;
    }

    public PositionSpec getPos() {
        return pos == null ? PositionSpec.DEFAULT : pos;
    }

    public int getCount() {
        return count;
    }

    public Vector3 getDelta() {
        return delta == null ? new Vector3(1.0, 1.0, 1.0) : delta;
    }

    public double getSpeed() {
        return speed;
    }

}
