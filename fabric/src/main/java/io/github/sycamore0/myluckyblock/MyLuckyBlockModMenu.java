package io.github.sycamore0.myluckyblock;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.sycamore0.myluckyblock.config.ModConfig;
import io.github.sycamore0.myluckyblock.config.ModConfigManager;
import io.github.sycamore0.myluckyblock.screen.ConfigScreen;

public class MyLuckyBlockModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            ModConfig config = ModConfigManager.loadConfig();
            return new ConfigScreen(screen, config);
        };
    }
}