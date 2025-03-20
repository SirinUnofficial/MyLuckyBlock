package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.Blocks.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.Items.createItems(Constants.MOD_ID);

    public static final String MY_LUCKY_BLOCK_ID = "my_lucky_block";

    public static final DeferredHolder<Block, Block> MY_LUCKY_BLOCK;

    static {
        MY_LUCKY_BLOCK = BLOCKS.register(MY_LUCKY_BLOCK_ID,
                () -> new LuckyBlock((BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).strength(0.5f).explosionResistance(5000000.0f))));
        ITEMS.register(MY_LUCKY_BLOCK_ID,
                () -> new BlockItem(MY_LUCKY_BLOCK.get(), new Item.Properties()));
    }

    public static void onInitialize(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
