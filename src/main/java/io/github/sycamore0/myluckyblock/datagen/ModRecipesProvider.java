package io.github.sycamore0.myluckyblock.datagen;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipesProvider extends FabricRecipeProvider {
    public ModRecipesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.MY_LUCKY_BLOCK, 1)
                .pattern("GGG")
                .pattern("GRG")
                .pattern("GGG")
                .input('G', Items.GOLD_INGOT)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion("has_item", RecipeProvider.conditionsFromItem(Items.GOLD_INGOT))
                .criterion("has_item", RecipeProvider.conditionsFromItem(Items.REDSTONE_BLOCK))
                .criterion("has_item", RecipeProvider.conditionsFromItem(ModBlocks.MY_LUCKY_BLOCK))
                .offerTo(exporter, Identifier.of(MyLuckyBlock.MOD_ID, "my_lucky_block"));
    }
}
