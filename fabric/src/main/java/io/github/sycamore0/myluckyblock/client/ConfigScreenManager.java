package io.github.sycamore0.myluckyblock.client;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Function;

public final class ConfigScreenManager {
    private static Function<Screen, Screen> factory;

    private ConfigScreenManager() {}

    public static void setFactory(Function<Screen, Screen> screenFactory) {
        factory = screenFactory;
    }

    public static boolean hasFactory() {
        return factory != null;
    }

    public static Screen create(Screen parent) {
        if (factory == null) {
            throw new IllegalStateException("No config screen factory registered!");
        }
        return factory.apply(parent);
    }
}
