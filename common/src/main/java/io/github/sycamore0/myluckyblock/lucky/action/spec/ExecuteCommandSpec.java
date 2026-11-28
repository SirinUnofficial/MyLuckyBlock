package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class ExecuteCommandSpec {
    @SerializedName("command")
    protected String command;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    public String getCommand() {
        return command;
    }

    public PositionSpec getPos() {
        return pos == null ? PositionSpec.DEFAULT : pos;
    }
}
