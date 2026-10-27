package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.item.LuckyBlockItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class FabricAddonLoader {
    private static final List<Block> ADDON_BLOCKS = new ArrayList<>();
    private static boolean scanned = false;

    private FabricAddonLoader() {}

    public static void load() {
        if (scanned) return;
        scanned = true;

        Path dir = FabricLoader.getInstance().getConfigDir().resolve("myluckyblock/addons");
        if (!Files.isDirectory(dir)) {
            Constants.LOG.info("[Addon] No addons directory at {}, skipping.", dir);
            return;
        }

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
            for (Path addonDir : ds) {
                if (!Files.isDirectory(addonDir)) continue;
                String name = addonDir.getFileName().toString();
                if (name.startsWith(".")) continue;
                scanAddonDir(addonDir);
            }
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to scan {}", dir, e);
        }

        Constants.LOG.info("[Addon] Parsed {} lucky block definition(s)", ADDON_BLOCKS.size());
    }

    private static void scanAddonDir(Path addonDir) {
        Path luckyDir = addonDir.resolve("lucky");
        if (!Files.isDirectory(luckyDir)) return;

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(luckyDir, "*.json")) {
            for (Path jsonFile : ds) {
                try (Reader reader = Files.newBufferedReader(jsonFile, StandardCharsets.UTF_8)) {
                    AddonLuckyBlockDefinition def = Constants.GSON.fromJson(reader, AddonLuckyBlockDefinition.class);
                    if (def == null || def.id() == null || def.id().isBlank()) {
                        Constants.LOG.warn("[Addon] Skip invalid definition: {}", jsonFile);
                        continue;
                    }
                    ResourceLocation id = ResourceLocation.tryParse(def.id());
                    if (id == null) {
                        Constants.LOG.warn("[Addon] Invalid ResourceLocation: {}", def.id());
                        continue;
                    }
                    registerBlock(def, id);
                    Constants.LOG.info("[Addon] Parsed block {} from {}/{}", def.id(), addonDir.getFileName(), jsonFile.getFileName());
                } catch (Exception e) {
                    Constants.LOG.error("[Addon] Failed to parse {}", jsonFile, e);
                }
            }
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to list {}", luckyDir, e);
        }
    }

    private static void registerBlock(AddonLuckyBlockDefinition def, ResourceLocation id) {
        AddonLuckyBlockDefinition.Properties p = def.propertiesOrDefault();
        String group = def.eventPackGroupNameOrDefault(Constants.EVENT_PACK_GROUP_NAME);

        Block block = new LuckyBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_PINK)
                        .strength(p.strengthOrDefault())
                        .explosionResistance(p.explosionResistanceOrDefault()),
                group
        );

        Registry.register(BuiltInRegistries.BLOCK, id, block);
        Registry.register(BuiltInRegistries.ITEM, id, new LuckyBlockItem(block, new Item.Properties()));

        ADDON_BLOCKS.add(block);
    }

    public static List<Block> getAddonBlocks() {
        return ADDON_BLOCKS;
    }
}