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

        Path addonsDir = FMLPaths.CONFIGDIR.get().resolve(Constants.ADDONS_PATH);
        for (AddonSource source : AddonSource.scan(addonsDir)) {
            for (AddonLuckyBlockDefinition definition : source.definitions()) {
                PENDING_DEFINITIONS.add(definition);
                Constants.LOG.info("[Addon] Queued block {} from {}", definition.id(), source.fileName());
            }
        }

        Constants.LOG.info("[Addon] Parsed {} lucky block definition(s) from addon archives", PENDING_DEFINITIONS.size());
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
