package io.github.sycamore0.myluckyblock.item;

import io.github.sycamore0.myluckyblock.block.LuckyBlockData;
import io.github.sycamore0.myluckyblock.block.LuckyBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LuckyBlockItem extends BlockItem {
    public LuckyBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !context.getLevel().isClientSide) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            if (level.getBlockEntity(pos) instanceof LuckyBlockEntity be) {
                be.applyData(LuckyBlockData.fromItemStack(context.getItemInHand()));
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, tooltip, flag);

        LuckyBlockData data = LuckyBlockData.fromItemStack(itemStack);
        LuckyBlockData.EventProbabilities p = LuckyBlockData.probabilitiesFor(data.luckyValue());

        ChatFormatting valueColor = data.luckyValue() > 0 ? ChatFormatting.GREEN : data.luckyValue() < 0 ? ChatFormatting.RED : ChatFormatting.GRAY;
        tooltip.add(Component.translatable("tooltip.myluckyblock.lucky_value", Component.literal(Integer.toString(data.luckyValue())).withStyle(valueColor)).withStyle(ChatFormatting.GRAY));

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.myluckyblock.prob.common", Component.literal(p.common() + "%").withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.myluckyblock.prob.lucky", Component.literal(p.lucky() + "%").withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.myluckyblock.prob.unlucky", Component.literal(p.unlucky() + "%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.GRAY));

            if (data.isLimitedOutcomes() && !data.outcomes().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.myluckyblock.limited_outcomes").withStyle(ChatFormatting.GOLD));
                for (String outcome : data.outcomes()) {
                    tooltip.add(Component.literal(" - " + outcome).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        } else {
            tooltip.add(Component.translatable("tooltip.myluckyblock.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}