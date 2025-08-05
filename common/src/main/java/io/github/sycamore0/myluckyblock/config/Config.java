package io.github.sycamore0.myluckyblock.config;

public class Config {
    public boolean generate_lucky_block = true;

    public void load() {
        Config loadedConfig = ConfigManager.loadConfig();
        this.generate_lucky_block = loadedConfig.generate_lucky_block;
    }

    public void save() {
        ConfigManager.saveConfig(this);
    }
}