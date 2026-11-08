package io.github.sycamore0.myluckyblock.lucky.action;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface LuckyAction {
    void execute(ActionContext context, JsonObject step) throws Exception;
}
