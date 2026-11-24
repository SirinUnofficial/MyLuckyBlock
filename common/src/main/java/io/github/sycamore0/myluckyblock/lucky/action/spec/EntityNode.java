package io.github.sycamore0.myluckyblock.lucky.action.spec;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EntityNode {
    @SerializedName("id")
    protected String id;

    @SerializedName("nbt")
    protected String nbt = null;

    @SerializedName("name")
    protected String name = null;

    @SerializedName("name_visible")
    protected boolean nameVisible = false;

    @SerializedName("is_baby")
    protected boolean isBaby = false;

    @SerializedName("passengers")
    protected List<EntityNode> passengers = null;

    public String getId() {
        return id;
    }

    public String getNbt() {
        return nbt;
    }

    public String getName() {
        return name;
    }

    public boolean isNameVisible() {
        return nameVisible;
    }

    public boolean isBaby() {
        return isBaby;
    }

    public List<EntityNode> getPassengers() {
        return passengers == null ? List.of() : passengers;
    }
}
