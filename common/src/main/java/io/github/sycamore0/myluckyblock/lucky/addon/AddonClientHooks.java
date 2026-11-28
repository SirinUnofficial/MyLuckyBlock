package io.github.sycamore0.myluckyblock.lucky.addon;

import java.util.function.Consumer;

public final class AddonClientHooks {
    private static volatile Consumer<AddonManifest> handler = manifest -> {};

    private AddonClientHooks() {}

    public static void set(Consumer<AddonManifest> consumer) {
        handler = consumer;
    }

    public static void handle(AddonManifest manifest) {
        handler.accept(manifest);
    }
}
