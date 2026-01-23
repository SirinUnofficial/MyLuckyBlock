package io.github.sycamore0.myluckyblock;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Constants {
    public static final String MOD_ID = "myluckyblock";
    public static final String MOD_NAME = "MyLuckyBlock";

    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON = new Gson();

    public static final List<String> EVENT_PACK_GROUP_LIST = new ArrayList<>();
}
