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

        Path addonsDir = FabricLoader.getInstance().getConfigDir().resolve(Constants.ADDONS_PATH);
        for (AddonSource source : AddonSource.scan(addonsDir)) {
            for (AddonLuckyBlockDefinition definition : source.definitions()) {
                ResourceLocation id = ResourceLocation.tryParse(definition.id());
                if (id == null) continue;
                if (BuiltInRegistries.BLOCK.containsKey(id)) {
                    Constants.LOG.warn("[Addon] Block {} already registered, ignoring {}", id, source.fileName());
                    continue;
                }
                registerBlock(definition, id);
                Constants.LOG.info("[Addon] Registered block {} from {}", id, source.fileName());
            }
        }

        Constants.LOG.info("[Addon] Registered {} lucky block(s) from addon archives", ADDON_BLOCKS.size());
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
