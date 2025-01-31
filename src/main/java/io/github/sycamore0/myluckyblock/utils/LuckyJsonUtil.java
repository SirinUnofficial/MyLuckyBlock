package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.MyLuckyBlock;

public class LuckyJsonUtil {
    private static final Gson GSON = new Gson();

    public static LuckyDataReader loadJsonData(JsonObject json) {
        try {
            return GSON.fromJson(json, LuckyDataReader.class);
        } catch (Exception e) {
            MyLuckyBlock.LOGGER.error("Failed to parse JSON: {}", json.get("fileName"), e);
            return null;
        }
    }
}