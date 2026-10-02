package io.github.sycamore0.myluckyblock.client;

import io.github.sycamore0.myluckyblock.platform.Services;
import net.fabricmc.api.ClientModInitializer;

public class MyLuckyBlockClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (Services.PLATFORM.isModLoaded("cloth-config")) {
            ConfigScreenManager.setFactory(ConfigScreen::create);
        }
    }
}