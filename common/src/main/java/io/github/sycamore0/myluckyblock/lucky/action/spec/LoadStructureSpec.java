package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.Constants;

public class LoadStructureSpec {
    @SerializedName("id")
    protected String id;

    @SerializedName("mod_id")
    protected String modId = Constants.MOD_ID;

    @SerializedName("pos")
    protected PositionSpec pos = new PositionSpec();

    public String getId() {
        return id;
    }

    public PositionSpec getPos() {
        return pos == null ? PositionSpec.DEFAULT : pos;
    }

    public String getModId() {
        return modId == null ? Constants.MOD_ID : modId;
    }

}
