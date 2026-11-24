package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.PlaceChestSpec;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public final class PlaceChestsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (PlaceChestSpec chest : LuckyActions.spec(step, PlaceChestSpec.class)) {
            placeChest(context.level(), PosHelper.parseVec3d(context.resolvePos(chest.getPosSrc(), chest.getOffset())), chest.getChestId(), chest.getId(), chest.getSeed());
        }
    }

    private static void placeChest(ServerLevel serverLevel, BlockPos blockPos, String chestBlockId, String lootTableId, long seed) {
        Block chestBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(chestBlockId));
        BlockState chestBlockState = chestBlock.defaultBlockState();
        serverLevel.setBlockAndUpdate(blockPos, chestBlockState);
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        if (blockEntity instanceof RandomizableContainer lootableInventory) {
            ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableId));
            lootableInventory.setLootTable(lootTable);
            lootableInventory.setLootTableSeed(seed);
        }
    }
}
