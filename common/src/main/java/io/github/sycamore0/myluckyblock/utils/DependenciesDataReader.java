package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.annotations.SerializedName;

public class DependenciesDataReader {
    @SerializedName("id")
    protected String id;

    @SerializedName("version_range")
    protected String versionRange;

    public DependenciesDataReader() {
        // Default Construct
        this.id = null;
        this.versionRange = null; // e.p. "[1.0.1,2.4.3)"
    }

    public String getId() {
        return id;
    }

    public String getVersionRange() {
        return versionRange;
    }
}
