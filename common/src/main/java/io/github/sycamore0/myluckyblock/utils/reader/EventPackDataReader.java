package io.github.sycamore0.myluckyblock.utils.reader;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventPackDataReader {
    @SerializedName("name")
    protected String name;

    @SerializedName("version")
    protected String version;

    @SerializedName("info")
    protected String info;

    @SerializedName("dependencies")
    protected List<DependenciesDataReader> dependencies;

    @SerializedName("random_events")
    protected List<RandomEventDataReader> randomEvents;

    public EventPackDataReader() {
        this.name = "unknown";
        this.version = "unknown";
        this.info = "unknown";
        this.dependencies = new ArrayList<>();
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

    public List<DependenciesDataReader> getDependencies() {
        return dependencies;
    }

    public List<RandomEventDataReader> getRandomEvents() {
        return randomEvents;
    }
}