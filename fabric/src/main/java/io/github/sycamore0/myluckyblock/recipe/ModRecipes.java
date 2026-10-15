package io.github.sycamore0.myluckyblock.recipe;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class ModRecipes {
    private ModRecipes() {}

    public static void onInitialize() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, ModRecipesCommon.ID);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, ModRecipesCommon.LUCKY_BLOCK_MODIFIER_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, ModRecipesCommon.LUCKY_BLOCK_MODIFIER_SERIALIZER);
    }
}