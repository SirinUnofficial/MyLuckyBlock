package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.addon.NeoForgeAddonLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LuckyBlockEntity>> LUCKY_BLOCK;

    static {
        LUCKY_BLOCK = BLOCK_ENTITIES.register("lucky_block", () -> {
            List<Block> blocks = new ArrayList<>();
            blocks.add(ModBlocks.MY_LUCKY_BLOCK.get());
            for (Holder<Block> blockHolder : NeoForgeAddonLoader.getAddonBlocks()) {
                blocks.add(blockHolder.value());
            }
            return BlockEntityType.Builder.of(LuckyBlockEntity::new, blocks.toArray(Block[]::new)).build(null);
        });

        LuckyBlockEntity.setTypeSupplier(LUCKY_BLOCK);
    }

    public static void onInitialize(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}