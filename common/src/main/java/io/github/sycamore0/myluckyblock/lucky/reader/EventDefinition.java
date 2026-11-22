package io.github.sycamore0.myluckyblock.lucky.reader;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.lucky.EventType;

import java.util.List;

public class EventDefinition {
    private EventType type;

    @SerializedName("id")
    protected int id;

    @SerializedName("steps")
    protected List<JsonObject> steps;

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public List<JsonObject> getSteps() {
        return steps == null ? List.of() : steps;
    }

    public int getEventId() {
        return id;
    }

    public void setEventId(int id) {
        this.id = id;
    }
}
