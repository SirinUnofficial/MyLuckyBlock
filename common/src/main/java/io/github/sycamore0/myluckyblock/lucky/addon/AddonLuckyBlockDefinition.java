package io.github.sycamore0.myluckyblock.lucky.addon;

import com.google.gson.annotations.SerializedName;

public record AddonLuckyBlockDefinition(
        String id,
        Properties properties,
        @SerializedName("event_pack_group_name") String eventPackGroupName
) {
    public static final float DEFAULT_STRENGTH = 0.5f;
    public static final float DEFAULT_EXPLOSION_RESISTANCE = 5000000.0f;

    public record Properties(
            Float strength,
            @SerializedName("explosion_resistance") Float explosionResistance
    ) {
        public static final Properties EMPTY = new Properties(null, null);

        public float strengthOrDefault() {
            return strength != null ? strength : DEFAULT_STRENGTH;
        }

        public float explosionResistanceOrDefault() {
            return explosionResistance != null ? explosionResistance : DEFAULT_EXPLOSION_RESISTANCE;
        }
    }

    public Properties propertiesOrDefault() {
        return properties != null ? properties : Properties.EMPTY;
    }

    public String eventPackGroupNameOrDefault(String fallback) {
        return (eventPackGroupName != null && !eventPackGroupName.isBlank()) ? eventPackGroupName : fallback;
    }
}