package io.github.sycamore0.myluckyblock.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.platform.Services;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!Services.PLATFORM.isModLoaded("cloth-config")) {
            Constants.LOG.warn("Cloth Config not found, skipping config screen registration.");
            return null;
        }
        else {
            return ConfigScreen::create;
        }
    }
}