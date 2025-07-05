package io.github.sycamore0.myluckyblock.platform;

import io.github.sycamore0.myluckyblock.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString()).orElse(null);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
