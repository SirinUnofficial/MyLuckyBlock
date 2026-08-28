package io.github.sycamore0.myluckyblock;

public class Config {
    public static boolean ENABLE_CREATIVE_TRIGGER = true; // Enable creative player to trigger events
    public static boolean LUCK_ENABLED = false; // (Experiment) Enable events trigger by player's luck/unluck effect
    public static final double LUCK_BASE = 5.0f; // (Experiment) Basic luck prob for luck event
    public static final double UNLUCK_BASE = 1.0f; // (Experiment) Basic luck prob for unluck event
    public static final double LUCK_PER_LEVEL = 1.0f; // (Experiment) Addition luck prob by effect
    public static final double UNLUCK_PER_LEVEL = 1.0f; // (Experiment) Addition unluck prob by effect
}
