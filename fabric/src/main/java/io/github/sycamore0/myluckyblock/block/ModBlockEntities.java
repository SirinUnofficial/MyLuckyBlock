package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.addon.FabricAddonLoader;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public class ModBlockEntities {
    public static final BlockEntityType<LuckyBlockEntity> LUCKY_BLOCK = register("lucky_block",
            FabricBlockEntityTypeBuilder.create(
                    LuckyBlockEntity::new,
                    combineBlocks()
            ));

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, FabricBlockEntityTypeBuilder<T> builder) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, builder.build());
    }

    private static Block[] combineBlocks() {
        List<Block> list = new ArrayList<>();
        list.add(ModBlocks.MY_LUCKY_BLOCK);
        list.addAll(FabricAddonLoader.getAddonBlocks());
        return list.toArray(Block[]::new);
    }

    public static void onInitialize() {
        LuckyBlockEntity.setTypeSupplier(() -> LUCKY_BLOCK);
    }
}