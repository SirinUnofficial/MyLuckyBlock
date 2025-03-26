package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;

public class LuckyJsonUtil {
    private static final Gson GSON = new Gson();

    public static LuckyEventDataReader loadJsonData(JsonObject json) {
        try {
            return GSON.fromJson(json, LuckyEventDataReader.class);
        } catch (Exception e) {
            Constants.LOG.error("Failed to parse JSON: {}", json.get("fileName"), e);
            return null;
        }
    }
}