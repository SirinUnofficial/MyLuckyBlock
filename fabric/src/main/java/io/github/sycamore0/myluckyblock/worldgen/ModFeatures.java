package io.github.sycamore0.myluckyblock.worldgen;

import io.github.sycamore0.myluckyblock.Constants;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ModFeatures {
    public static final TagKey<Biome> LB_GEN_BIOME_TAG = TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lb_gen"));

    public static void onInitialize() {
        BiomeModifications.addFeature(
                biomeSelectionContext -> biomeSelectionContext.hasTag(LB_GEN_BIOME_TAG),
                GenerationStep.Decoration.SURFACE_STRUCTURES,
                ResourceKey.create(Registries.PLACED_FEATURE,
                        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lucky_block_surface"))
        );
    }
}