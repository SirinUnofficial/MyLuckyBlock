package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.Blocks.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.Items.createItems(Constants.MOD_ID);

    public static final String MY_LUCKY_BLOCK_ID = "my_lucky_block";

    public static final DeferredHolder<Block, Block> MY_LUCKY_BLOCK;
    public static final DeferredHolder<Item, BlockItem> MY_LUCKY_BLOCK_ITEM;

    static {
        MY_LUCKY_BLOCK = BLOCKS.register(MY_LUCKY_BLOCK_ID,
                registryName -> new LuckyBlock(BlockBehaviour.Properties.of()
                        .setId(ResourceKey.create(Registries.BLOCK, registryName))
                        .destroyTime(0.5f)
                        .explosionResistance(5000000.0f)
                ));
        MY_LUCKY_BLOCK_ITEM = ITEMS.register(MY_LUCKY_BLOCK_ID,
                registryName -> new BlockItem(MY_LUCKY_BLOCK.get(), new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, registryName))
                ));
    }

    public static void onInitialize(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
