package io.github.sycamore0.myluckyblock.client;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.platform.Services;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class MyLuckyBlockClient {
    private MyLuckyBlockClient() {}

    public static void registerConfigScreen(ModContainer container) {
        if (!Services.PLATFORM.isModLoaded("cloth_config")) {
            Constants.LOG.warn("Cloth Config not found, skipping config screen registration.");
            return;
        }

        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (modContainer, parent) -> ConfigScreen.create(parent)
        );
    }
}