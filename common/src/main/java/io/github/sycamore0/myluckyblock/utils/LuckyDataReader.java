package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LuckyDataReader {
    @SerializedName("name")
    protected String name;

    @SerializedName("version")
    protected String version;

    @SerializedName("info")
    protected String info;

    @SerializedName("random_events")
    protected List<LuckyEventReader> randomEvents;

    public LuckyDataReader() {
        // Default Construct
        this.name = "unknown";
        this.version = "unknown";
        this.info = "unknown";
        this.randomEvents = new ArrayList<>();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getInfo() {
        return info;
    }

    public List<LuckyEventReader> getRandomEvents() {
        return randomEvents;
    }
}