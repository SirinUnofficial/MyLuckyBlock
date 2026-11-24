package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.ActionSupport;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.DropItem;
import io.github.sycamore0.myluckyblock.utils.helper.DataComponentsHelper;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class DropItemsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (DropItem item : LuckyActions.spec(step, DropItem.class)) {
            int count = ActionSupport.roll(item.isUseRandom(), item.getRandomNum(), item.getNum());
            Vec3 pos = ActionSupport.spread(context.level(), context.resolvePos(item.getPosSrc(), item.getOffset()), item.getSpread());
            dropItems(context.level(), pos, item.getId(), count, item.getName(), item.isNameVisible(), item.getDesc(), item.getNbt(), item.getDataComponents());
        }
    }

    private static void dropItems(ServerLevel serverLevel, Vec3 pos, String itemId, int count, @Nullable String name, boolean nameVisible, @Nullable String desc, @Nullable String nbtString, @Nullable JsonObject dataComponents) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item.equals(Items.AIR)) return;
        ItemStack itemStack = new ItemStack(item, count);

        if (nbtString != null) {
            itemStack = NbtHelper.createItemStackWithNBT(itemStack, nbtString, serverLevel.registryAccess());
        }

        if (name != null) {
            itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable(name));
        }

        if (desc != null) {
            ItemLore itemLore = new ItemLore(List.of(Component.translatable(desc)));
            itemStack.set(DataComponents.LORE, itemLore);
        }

        if (dataComponents != null && !dataComponents.isEmpty()) {
            DataComponentsHelper.applyItemComponents(itemStack, dataComponents, serverLevel.registryAccess());
        }

        ItemEntity itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y(), pos.z(), itemStack);
        if (name != null) {
            itemEntity.setCustomName(Component.translatable(name));
            itemEntity.setCustomNameVisible(nameVisible);
        }

        itemEntity.setPos(pos);
        serverLevel.addFreshEntity(itemEntity);
    }
}
