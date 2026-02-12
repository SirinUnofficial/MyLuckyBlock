package io.github.sycamore0.myluckyblock.utils.reader;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class DisabledDataReader {
    @SerializedName("replace")
    protected boolean replace;

    @SerializedName("values")
    protected List<String> values;

    public DisabledDataReader() {
        this.replace = false;
        this.values = new ArrayList<>();
    }

    public boolean isReplace() {
        return replace;
    }

    public List<String> getValues() {
        return values;
    }
}