package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.DropLootsSpec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public final class DropLootsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (DropLootsSpec loot : LuckyActions.spec(step, DropLootsSpec.class)) {
            dropLoots(context.level(), context.resolvePos(loot.getPosSrc(), loot.getOffset()), loot.getId(), loot.getSeed());
        }
    }

    private static void dropLoots(ServerLevel serverLevel, Vec3 pos, String lootTableId, long seed) {
        ResourceKey<LootTable> lootTableResourceKey = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableId));
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableResourceKey);
        LootParams.Builder builder = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, pos);
        ObjectArrayList<ItemStack> itemStacks = lootTable.getRandomItems(builder.create(LootContextParamSets.COMMAND), seed);

        for (ItemStack itemStack : itemStacks) {
            ItemEntity itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y(), pos.z(), itemStack);
            itemEntity.setPos(pos);
            serverLevel.addFreshEntity(itemEntity);
        }
    }
}
