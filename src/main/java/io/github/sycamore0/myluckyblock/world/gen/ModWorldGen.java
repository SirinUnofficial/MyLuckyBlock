package io.github.sycamore0.myluckyblock.world.gen;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import io.github.sycamore0.myluckyblock.world.feature.SurfaceLuckyBlockFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModWorldGen {
    public static final Feature<DefaultFeatureConfig> LUCKY_BLOCK_FEATURE =
            new SurfaceLuckyBlockFeature(ModBlocks.MY_LUCKY_BLOCK);

    public static void register() {
        Registry.register(Registries.FEATURE,
                Identifier.of(MyLuckyBlock.MOD_ID, "surface_lucky_block"),
                LUCKY_BLOCK_FEATURE
        );

        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE,
                        Identifier.of(MyLuckyBlock.MOD_ID, "placed_surface_lucky_block"))
        );
    }
}