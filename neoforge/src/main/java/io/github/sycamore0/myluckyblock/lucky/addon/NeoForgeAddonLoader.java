package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlock;
import io.github.sycamore0.myluckyblock.item.LuckyBlockItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class NeoForgeAddonLoader {
    private static final List<AddonLuckyBlockDefinition> PENDING_DEFINITIONS = new ArrayList<>();
    private static final Map<AddonLuckyBlockDefinition, Holder<Block>> REGISTERED_BLOCKS = new LinkedHashMap<>();
    private static boolean scanned = false;

    private NeoForgeAddonLoader() {
    }

    public static void load() {
        if (scanned) return;
        scanned = true;

        Path dir = FMLPaths.CONFIGDIR.get().resolve("myluckyblock/addons");
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

        Constants.LOG.info("[Addon] Parsed {} lucky block definition(s)", PENDING_DEFINITIONS.size());
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
                        Constants.LOG.warn("[Addon] Invalid ResourceLocation in {}: {}", jsonFile, def.id());
                        continue;
                    }
                    PENDING_DEFINITIONS.add(def);
                    Constants.LOG.info("[Addon] Parsed block {} from {}/{}", def.id(), addonDir.getFileName(), jsonFile.getFileName());
                } catch (Exception e) {
                    Constants.LOG.error("[Addon] Failed to parse {}", jsonFile, e);
                }
            }
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to list {}", luckyDir, e);
        }
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            registerBlocks(event);
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            registerItems(event);
        }
    }

    private static void registerBlocks(RegisterEvent event) {
        for (AddonLuckyBlockDefinition definition : PENDING_DEFINITIONS) {
            ResourceLocation id = ResourceLocation.parse(definition.id());
            AddonLuckyBlockDefinition.Properties properties = definition.propertiesOrDefault();
            String eventPackGroupName = definition.eventPackGroupNameOrDefault(Constants.EVENT_PACK_GROUP_NAME);

            event.register(Registries.BLOCK, id, () -> new LuckyBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PINK)
                            .strength(properties.strengthOrDefault())
                            .explosionResistance(properties.explosionResistanceOrDefault()),
                    eventPackGroupName
            ));

            Holder<Block> holder = BuiltInRegistries.BLOCK.getHolderOrThrow(ResourceKey.create(Registries.BLOCK, id));
            REGISTERED_BLOCKS.put(definition, holder);
            Constants.LOG.info("[Addon] Registered block {}", id);
        }
    }

    private static void registerItems(RegisterEvent event) {
        for (Map.Entry<AddonLuckyBlockDefinition, Holder<Block>> entry : REGISTERED_BLOCKS.entrySet()) {
            AddonLuckyBlockDefinition def = entry.getKey();
            Holder<Block> blockHolder = entry.getValue();
            ResourceLocation id = ResourceLocation.parse(def.id());
            event.register(Registries.ITEM, id, () -> new LuckyBlockItem(blockHolder.value(), new Item.Properties()));
            Constants.LOG.info("[Addon] Registered item {}", id);
        }
    }

    public static List<Holder<Block>> getAddonBlocks() {
        return new ArrayList<>(REGISTERED_BLOCKS.values());
    }
}