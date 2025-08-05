package io.github.sycamore0.myluckyblock;

import io.github.sycamore0.myluckyblock.config.ModConfigNeo;
import io.github.sycamore0.myluckyblock.screen.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class MyLuckyBlockClient {
    public MyLuckyBlockClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, ModConfigNeo.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, (MC, parent) -> new ConfigScreen(parent));
    }
}
