package io.github.sycamore0.myluckyblock.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.platform.Services;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "myluckyblock.json";

    private static ModConfig config = new ModConfig();
    private static Path configPath;

    private static final String MESSAGE_WARN_NULL_CONFIG_PATH = "ConfigManager not initialized yet!";

    private ConfigManager() {}

    public static void init() {
        configPath = Services.PLATFORM.getConfigDir().resolve(FILE_NAME);
        load();
    }

    public static ModConfig get() {
        return config;
    }

    public static void load() {
        if (configPath == null) {
            Constants.LOG.warn(MESSAGE_WARN_NULL_CONFIG_PATH);
            return;
        }
        if (!Files.exists(configPath)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(configPath)) {
            ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
            if (loaded != null) {
                config = loaded;
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to load config", e);
        }
    }

    public static void save() {
        if (configPath == null) {
            Constants.LOG.warn(MESSAGE_WARN_NULL_CONFIG_PATH);
            return;
        }
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }
}