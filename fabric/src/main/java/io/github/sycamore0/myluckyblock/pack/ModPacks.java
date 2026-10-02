package io.github.sycamore0.myluckyblock.pack;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ModPacks {
    private static final ModContainer MOD_CONTAINER = FabricLoader.getInstance()
            .getModContainer(Constants.MOD_ID)
            .orElseThrow(() -> new RuntimeException("Mod " + Constants.MOD_ID + " not found"));

    private static ResourceLocation locate(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static void addBuiltInDataPack(String packName, boolean defaultEnabled) {
        ResourceManagerHelperImpl.registerBuiltinResourcePack(
                locate(packName),
                "datapacks/" + packName,
                MOD_CONTAINER,
                Component.translatable("pack.name." + packName),
                defaultEnabled ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL
        );
    }

    public static void onInitialize() {
        addBuiltInDataPack("disable_lucky_block_worldgen", ConfigManager.get().autoDisableLuckyBlockWorldgen);
        addBuiltInDataPack("disable_lucky_block_structure", ConfigManager.get().autoDisableLuckyBlockStructure);
        addBuiltInDataPack("disable_lucky_block_boss", ConfigManager.get().autoDisableLuckyBlockBoss);
        addBuiltInDataPack("disable_lucky_block_explosions", ConfigManager.get().autoDisableLuckyBlockExplosions);
    }
}
