package io.github.sycamore0.myluckyblock.config;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "myluckyblock.json";

    public static Path getConfigDir() {
        Path gameDir;
        try {
            gameDir = Minecraft.getInstance().gameDirectory.toPath();
        } catch (Exception e) {
            gameDir = new File(".").toPath();
            Constants.LOG.error(String.valueOf(e)); // WHY???
        }

        return gameDir.resolve("config");
    }

    public static Path getConfigFilePath() {
        return getConfigDir().resolve(CONFIG_FILE_NAME);
    }

    public static void saveConfig(Config config) {
        Path configFilePath = getConfigFilePath();
        try (FileWriter writer = new FileWriter(configFilePath.toFile())) {
            Constants.GSON.toJson(config, writer);
        } catch (IOException e) {
            Constants.LOG.error(String.valueOf(e));
        }
    }

    public static Config loadConfig() {
        Path configFilePath = getConfigFilePath();
        if (configFilePath.toFile().exists()) {
            try (FileReader reader = new FileReader(configFilePath.toFile())) {
                return Constants.GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                Constants.LOG.error(String.valueOf(e));
            }
        }
        return new Config();
    }
}