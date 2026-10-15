package io.github.sycamore0.myluckyblock.recipe;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Constants.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Constants.MOD_ID);

    static {
        RECIPE_TYPES.register(ModRecipesCommon.ID, () -> ModRecipesCommon.LUCKY_BLOCK_MODIFIER_TYPE);
        RECIPE_SERIALIZERS.register(ModRecipesCommon.ID, () -> ModRecipesCommon.LUCKY_BLOCK_MODIFIER_SERIALIZER);
    }

    private ModRecipes() {
    }

    public static void onInitialize(IEventBus eventBus) {
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}