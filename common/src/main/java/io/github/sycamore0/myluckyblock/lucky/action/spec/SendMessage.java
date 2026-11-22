package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

public class SendMessage {
    @SerializedName("msg")
    protected String msg;

    public String getMsg() {
        return msg;
    }
}
