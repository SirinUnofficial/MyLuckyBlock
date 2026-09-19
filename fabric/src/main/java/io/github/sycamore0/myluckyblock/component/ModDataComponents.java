package io.github.sycamore0.myluckyblock.component;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModDataComponents {
    public static final DataComponentType<LuckyBlockData> LUCKY_BLOCK_DATA =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_block_data"),
                    DataComponentType.<LuckyBlockData>builder()
                            .persistent(LuckyBlockData.CODEC)
                            .networkSynchronized(LuckyBlockData.STREAM_CODEC)
                            .build()
            );

    public static void onInitialize() {
        ModDataComponentsCommon.setLuckyBlockData(() -> LUCKY_BLOCK_DATA);
    }
}
