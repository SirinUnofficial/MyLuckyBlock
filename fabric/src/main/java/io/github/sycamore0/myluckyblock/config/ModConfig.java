package io.github.sycamore0.myluckyblock.config;

public class ModConfig {
    public boolean generate_lucky_block = true;

    public void load() {
        ModConfig loadedConfig = ModConfigManager.loadConfig();
        this.generate_lucky_block = loadedConfig.generate_lucky_block;
    }

    public void save() {
        ModConfigManager.saveConfig(this);
    }
}