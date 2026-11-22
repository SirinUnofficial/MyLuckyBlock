package io.github.sycamore0.myluckyblock.lucky.reader;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EventPack {
    @SerializedName("name")
    protected String name;

    @SerializedName("version")
    protected String version;

    @SerializedName("info")
    protected String info;

    @SerializedName("dependencies")
    protected List<DependenciesDataReader> dependencies;

    @SerializedName("random_events")
    protected List<EventDefinition> randomEvents;

    private transient String packKey;

    public EventPack() {
        this.name = "unknown";
        this.version = "unknown";
        this.info = "unknown";
        this.dependencies = new ArrayList<>();
        this.randomEvents = new ArrayList<>();
    }

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

    public List<EventDefinition> getRandomEvents() {
        return randomEvents;
    }

    @Nullable
    public String getPackKey() {
        return packKey;
    }

    public void setPackKey(String packKey) {
        this.packKey = packKey;
    }
}