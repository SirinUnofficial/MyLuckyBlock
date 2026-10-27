package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public record AddonPackProvider(PackType type) implements RepositorySource {
    private static final PackSelectionConfig SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.TOP, false);
    private static final PackSource SOURCE = PackSource.BUILT_IN;

    @Override
    public void loadPacks(Consumer<Pack> packAdder) {
        Path addonsDir = FabricLoader.getInstance().getConfigDir().resolve("myluckyblock/addons");
        if (!Files.isDirectory(addonsDir)) return;

        DirectoryValidator validator = new DirectoryValidator(p -> true);
        try {
            FolderRepositorySource.discoverPacks(addonsDir, validator, (path, factory) -> {
                String fileName = path.getFileName().toString();
                PackLocationInfo loc = new PackLocationInfo(
                        "myluckyblock_addon/" + fileName,
                        Component.literal(fileName),
                        SOURCE,
                        Optional.empty()
                );
                Pack pack = Pack.readMetaAndCreate(loc, factory, this.type, SELECTION_CONFIG);
                if (pack != null) {
                    packAdder.accept(pack);
                    Constants.LOG.info("[Addon] Registered {} as {} pack", fileName, this.type);
                } else {
                    Constants.LOG.warn("[Addon] Failed to read pack.mcmeta in {}", fileName);
                }
            });
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to scan addons directory", e);
        }
    }
}