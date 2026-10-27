package io.github.sycamore0.myluckyblock.pack;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ModPacks {
    @SubscribeEvent
    private static void setupDataPack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            addBuiltInDataPack(event, "disable_lucky_block_worldgen", ConfigManager.get().autoDisableLuckyBlockWorldgen);
            addBuiltInDataPack(event, "disable_lucky_block_structure", ConfigManager.get().autoDisableLuckyBlockStructure);
            addBuiltInDataPack(event, "disable_lucky_block_boss", ConfigManager.get().autoDisableLuckyBlockBoss);
            addBuiltInDataPack(event, "disable_lucky_block_explosions", ConfigManager.get().autoDisableLuckyBlockExplosions);
        }
        addAddonPacks(event);
    }

    private static void addBuiltInDataPack(AddPackFindersEvent event, String packName, boolean defaultEnabled) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "datapacks/" + packName),
                PackType.SERVER_DATA,
                Component.translatable("pack.name." + packName),
                PackSource.create(decorateWithSource("pack.source.builtin"), defaultEnabled),
                false,
                Pack.Position.TOP
        );
    }

    private static void addAddonPacks(AddPackFindersEvent event) {
        Path addonsDir = FMLPaths.CONFIGDIR.get().resolve("myluckyblock/addons");
        if (!Files.isDirectory(addonsDir)) return;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(addonsDir)) {
            for (Path addonDir : directoryStream) {
                if (!Files.isDirectory(addonDir)) continue;
                String fileName = addonDir.getFileName().toString();
                if (fileName.startsWith(".")) continue;

                if (!Files.exists(addonDir.resolve("pack.mcmeta"))) {
                    Constants.LOG.warn("[Addon] {} missing pack.mcmeta, skipping", fileName);
                    continue;
                }

                String packId = "myluckyblock_addon/" + fileName;
                PackLocationInfo location = new PackLocationInfo(packId, Component.literal(fileName), PackSource.DEFAULT, Optional.empty());
                PathPackResources.PathResourcesSupplier supplier = new PathPackResources.PathResourcesSupplier(addonDir);
                PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
                Pack pack = Pack.readMetaAndCreate(location, supplier, event.getPackType(), selectionConfig);

                if (pack != null) {
                    event.addRepositorySource(consumer -> consumer.accept(pack));
                    Constants.LOG.info("[Addon] Registered {} as {} pack", fileName, event.getPackType());
                } else {
                    Constants.LOG.warn("[Addon] Failed to read pack.mcmeta in {}", fileName);
                }
            }
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to scan addons directory", e);
        }
    }

    // net.minecraft.server.packs.repository.PackSource#decorateWithSource(String)
    private static UnaryOperator<Component> decorateWithSource(String translationKey) {
        Component component = Component.translatable(translationKey);
        return (_component) -> Component.translatable("pack.nameAndSource", _component, component).withStyle(ChatFormatting.GRAY);
    }

    public static void onInitialize(IEventBus eventBus) {
        eventBus.addListener(ModPacks::setupDataPack);
    }
}