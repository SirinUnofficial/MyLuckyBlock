package io.github.sycamore0.myluckyblock.component;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LuckyBlockData>> LUCKY_BLOCK_DATA =
            COMPONENTS.register("lucky_block_data", () -> DataComponentType.<LuckyBlockData>builder()
                    .persistent(LuckyBlockData.CODEC)
                    .networkSynchronized(LuckyBlockData.STREAM_CODEC)
                    .build());

    public static void onInitialize(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
        ModDataComponentsCommon.setLuckyBlockData(LUCKY_BLOCK_DATA);
    }
}