package io.github.sycamore0.myluckyblock;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final String MOD_ID = "myluckyblock";
    public static final String MOD_NAME = "MyLuckyBlock";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Map<String, List<JsonObject>> loadedEventPacks = new HashMap<>();
    public static final List<String> eventPackIdList = new ArrayList<>();
}
