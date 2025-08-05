package io.github.sycamore0.myluckyblock.config;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.file.Path;

public class ModConfigManager {
    private static final String CONFIG_FILE_NAME = "myluckyblock.properties";

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

    public static ModConfig loadConfig() {
        Path configFilePath = getConfigFilePath();
        File configFile = configFilePath.toFile();
        ModConfig config = new ModConfig();
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                Constants.PROPERTIES.load(fis);
                config.generate_lucky_block = Boolean.parseBoolean(Constants.PROPERTIES.getProperty("generate_lucky_block", "true"));
            } catch (IOException e) {
                Constants.LOG.error(String.valueOf(e));
            }
        }
        return config;
    }

    public static void saveConfig(ModConfig config) {
        Path configFilePath = getConfigFilePath();
        File configFile = configFilePath.toFile();
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            Constants.PROPERTIES.setProperty("generate_lucky_block", String.valueOf(config.generate_lucky_block));
            Constants.PROPERTIES.store(fos, "My Lucky Block Configuration");
        } catch (IOException e) {
            Constants.LOG.error(String.valueOf(e));
        }
    }
}