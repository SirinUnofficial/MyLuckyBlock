package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class DisplayMessageSpec {
    @SerializedName("msg")
    protected String msg;

    @SerializedName("overlay")
    protected boolean overlay = true;

    public boolean getOverlay() {
        return overlay;
    }

    public String getMsg() {
        return msg;
    }
}
