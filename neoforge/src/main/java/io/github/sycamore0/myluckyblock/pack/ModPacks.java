package io.github.sycamore0.myluckyblock.pack;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import io.github.sycamore0.myluckyblock.lucky.addon.AddonSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ModPacks {
    private static final PackSelectionConfig ADDON_SELECTION_CONFIG =
            new PackSelectionConfig(true, Pack.Position.BOTTOM, false);

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
        Path addonsDir = FMLPaths.CONFIGDIR.get().resolve(Constants.ADDONS_PATH);
        PackType packType = event.getPackType();

        for (AddonSource addon : AddonSource.scan(addonsDir)) {
            if (!addon.hasPackMeta()) {
                Constants.LOG.info("[Addon] {} has no pack.mcmeta, not registered as {} pack", addon.fileName(), packType);
                continue;
            }

            PackLocationInfo location = new PackLocationInfo(
                    "myluckyblock_addon/" + addon.baseName(),
                    Component.literal(addon.baseName()),
                    PackSource.DEFAULT,
                    Optional.empty()
            );

            try {
                Pack pack = Pack.readMetaAndCreate(
                        location,
                        new ZipPackResources.Supplier(addon.path(), addon.rootPrefix()),
                        packType,
                        ADDON_SELECTION_CONFIG
                );
                if (pack == null) {
                    Constants.LOG.warn("[Addon] Failed to read pack.mcmeta in {}", addon.fileName());
                    continue;
                }
                event.addRepositorySource(consumer -> consumer.accept(pack));
                Constants.LOG.info("[Addon] Registered {} as {} pack", addon.fileName(), packType);
            } catch (RuntimeException e) {
                Constants.LOG.error("[Addon] Failed to open {} as {} pack: {}", addon.fileName(), packType, e.getMessage());
            }
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