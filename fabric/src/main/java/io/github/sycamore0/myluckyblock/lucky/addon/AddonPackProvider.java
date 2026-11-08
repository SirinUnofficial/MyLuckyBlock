package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.pack.ZipPackResources;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public record AddonPackProvider(PackType type) implements RepositorySource {
    private static final PackSelectionConfig SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.TOP, false);
    private static final PackSource SOURCE = PackSource.BUILT_IN;

    @Override
    public void loadPacks(Consumer<Pack> packAdder) {
        Path addonsDir = FabricLoader.getInstance().getConfigDir().resolve(Constants.ADDONS_PATH);

        for (AddonSource addon : AddonSource.scan(addonsDir)) {
            if (!addon.hasPackMeta()) {
                Constants.LOG.info("[Addon] {} has no pack.mcmeta, not registered as {} pack", addon.fileName(), type);
                continue;
            }

            PackLocationInfo location = new PackLocationInfo(
                    "myluckyblock_addon/" + addon.baseName(),
                    Component.literal(addon.baseName()),
                    SOURCE,
                    Optional.empty()
            );
            try {
                Pack pack = Pack.readMetaAndCreate(
                        location,
                        new ZipPackResources.Supplier(addon.path(), addon.rootPrefix()),
                        this.type,
                        SELECTION_CONFIG
                );
                if (pack == null) {
                    Constants.LOG.warn("[Addon] Failed to read pack.mcmeta in {}", addon.fileName());
                    continue;
                }
                packAdder.accept(pack);
                Constants.LOG.info("[Addon] Registered {} as {} pack", addon.fileName(), this.type);
            } catch (RuntimeException e) {
                Constants.LOG.error("[Addon] Failed to open {} as {} pack: {}", addon.fileName(), this.type, e.getMessage());
            }
        }
    }
}
