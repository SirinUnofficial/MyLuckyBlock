package io.github.sycamore0.myluckyblock.pack;

import io.github.sycamore0.myluckyblock.Constants;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ModPacks {
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance()
            .getModContainer(Constants.MOD_ID)
            .orElseThrow(() -> new RuntimeException("Mod " + Constants.MOD_ID + " not found"));

    public static Identifier locate(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    public static void addBuiltInDataPack(String packName) {
        ResourceLoaderImpl.registerBuiltinPack(locate(packName), "datapacks/" + packName, MOD_CONTAINER, Component.translatable("pack.name." + packName), PackActivationType.NORMAL);
    }

    public static void onInitialize() {
        addBuiltInDataPack("disable_lucky_block_worldgen");
        addBuiltInDataPack("disable_lucky_block_structure");
    }
}
