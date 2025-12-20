package io.github.sycamore0.myluckyblock.utils.reader;

import com.google.gson.annotations.SerializedName;

public class DependenciesDataReader {
    @SerializedName("mod_id")
    protected String modId;

    @SerializedName("version_range")
    protected String versionRange;

    public DependenciesDataReader() {
        // Default Construct
        this.modId = null;
        this.versionRange = null; // e.p. "[1.0.1,2.4.3)"
    }

    public String getModId() {
        return modId;
    }

    public String getVersionRange() {
        return versionRange;
    }
}
