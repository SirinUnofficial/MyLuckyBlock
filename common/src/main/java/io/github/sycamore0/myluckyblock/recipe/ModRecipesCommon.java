package io.github.sycamore0.myluckyblock.recipe;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipesCommon {
    public static final String ID = "lucky_block_modifier";

    public static final RecipeType<LuckyBlockModifierRecipe> LUCKY_BLOCK_MODIFIER_TYPE =
            new RecipeType<>() {
                @Override
                public String toString() {
                    return Constants.MOD_ID + ":" + ID;
                }
            };

    public static final RecipeSerializer<LuckyBlockModifierRecipe> LUCKY_BLOCK_MODIFIER_SERIALIZER = new LuckyBlockModifierRecipe.Serializer();

    private ModRecipesCommon() {}
}