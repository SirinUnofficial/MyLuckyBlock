package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ModBlocks {
    public static final Block MY_LUCKY_BLOCK;

    static {
        MY_LUCKY_BLOCK = register("my_lucky_block", LuckyBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5f).explosionResistance(5000000.0f), Constants.MOD_ID);
    }

    public static Block register(String path, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties settings, String modId) {
        final Identifier identifier = Identifier.fromNamespaceAndPath(modId, path);
        final ResourceKey<Block> registryKey = ResourceKey.create(Registries.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        registerBlock(block, null, null);
        return block;
    }

    public static Block createNewLuckyBlock(String blockId, String modId, String eventPackGroupName, boolean includeBuiltIn, MapColor mapColor, float strength, float explosionResistance) {
        return register(blockId,
                (settings) -> new LuckyBlock(settings, eventPackGroupName, includeBuiltIn),
                BlockBehaviour.Properties.of().mapColor(mapColor).strength(strength).explosionResistance(explosionResistance),
                modId);
    }

    private static void registerBlock(Block block, BiFunction<Block, Item.Properties, Item> itemFactory, Item.Properties properties) {
        if (itemFactory == null) itemFactory = BlockItem::new;
        if (properties == null) properties = new Item.Properties();

        ResourceKey<Item> itemKey = ResourceKey.create(
                Registries.ITEM,
                block.builtInRegistryHolder().key().identifier()
        );
        Item.Properties finalProps = properties.useBlockDescriptionPrefix().setId(itemKey);
        Item item = itemFactory.apply(block, finalProps);
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    public static void onInitialize() {
    }
}
