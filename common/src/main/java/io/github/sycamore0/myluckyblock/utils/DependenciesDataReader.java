package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.annotations.SerializedName;

public class DependenciesDataReader {
    @SerializedName("id")
    protected String id;

    public DependenciesDataReader() {
        // Default Construct
        this.id = null;
    }

    public String getId() {
        return id;
    }
}
