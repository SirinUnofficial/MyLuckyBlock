package io.github.sycamore0.myluckyblock.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.ModBlocksCommon;
import io.github.sycamore0.myluckyblock.component.ModDataComponentsCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record LuckyBlockModifierRecipe(Ingredient modifier, int delta) implements CraftingRecipe {
    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        boolean hasLuckyBlock = false;
        boolean hasModifier = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModBlocksCommon.luckyBlock().asItem())) {
                if (hasLuckyBlock) return false;
                hasLuckyBlock = true;
            } else if (modifier.test(stack)) {
                if (hasModifier) return false;
                hasModifier = true;
            } else {
                return false;
            }
        }
        return hasLuckyBlock && hasModifier;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input, @NotNull HolderLookup.Provider registries) {
        ItemStack luckyStack = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (s.is(ModBlocksCommon.luckyBlock().asItem())) {
                luckyStack = s;
                break;
            }
        }
        if (luckyStack.isEmpty()) return ItemStack.EMPTY;

        LuckyBlockData oldData = LuckyBlockData.fromItemStack(luckyStack);
        int newValue = Mth.clamp(
                oldData.luckyValue() + delta,
                LuckyBlockData.MIN_LUCKY_VALUE,
                LuckyBlockData.MAX_LUCKY_VALUE
        );

        ItemStack result = new ItemStack(ModBlocksCommon.luckyBlock().asItem());
        LuckyBlockData newData = new LuckyBlockData(newValue, oldData.isLimitedOutcomes(), oldData.outcomes());
        if (!newData.isEmpty()) {
            result.set(ModDataComponentsCommon.luckyBlockData(), newData);
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return new ItemStack(ModBlocksCommon.luckyBlock().asItem());
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(ModBlocksCommon.luckyBlock().asItem()));
        list.add(modifier);
        return list;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return ModRecipesCommon.LUCKY_BLOCK_MODIFIER_SERIALIZER;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<LuckyBlockModifierRecipe> {
        public static final MapCodec<LuckyBlockModifierRecipe> CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("modifier").forGetter(LuckyBlockModifierRecipe::modifier),
                        Codec.INT.fieldOf("delta").forGetter(LuckyBlockModifierRecipe::delta)
                ).apply(inst, LuckyBlockModifierRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, LuckyBlockModifierRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, LuckyBlockModifierRecipe::modifier,
                        ByteBufCodecs.VAR_INT, LuckyBlockModifierRecipe::delta,
                        LuckyBlockModifierRecipe::new
                );

        @Override
        public @NotNull MapCodec<LuckyBlockModifierRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, LuckyBlockModifierRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}