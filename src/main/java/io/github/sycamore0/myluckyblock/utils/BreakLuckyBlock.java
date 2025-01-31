package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BreakLuckyBlock {
    public static LuckyDataManager manager = new LuckyDataManager();

    public static void breakLuckyBlock(World world, PlayerEntity player, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld) {
            if (state.getBlock() == ModBlocks.MY_LUCKY_BLOCK) {
                // 检查精准采集
                boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
                if (hasSilkTouch) {
                    LuckyFunctions.dropItems(world, PosHelper.parseBlockPos(pos), new ItemStack(ModBlocks.MY_LUCKY_BLOCK));
                    return;
                }

                // 执行随机事件
                LuckyEventReader randomFunction = manager.getRandomEvent();
                if (randomFunction != null) {
                    LuckyExecutor.executeLuckyFunction(world, player, pos, randomFunction);
                }
            } else if (state.getBlock() == ModBlocks.DEBUG_LUCKY_BLOCK) {
                // 检查精准采集
                boolean hasSilkTouch = EnchantmentsHelper.checkSilkTouch(player);
                if (hasSilkTouch) {
                    LuckyFunctions.dropItems(world, PosHelper.parseBlockPos(pos), new ItemStack(ModBlocks.DEBUG_LUCKY_BLOCK));
                    return;
                }

                // 执行所以事件
                for (int i = 1; i <= manager.getRandomEventsCount(); i++) {
                    LuckyEventReader randomFunction = manager.getEventById(i);
                    if (randomFunction != null) {
                        LuckyExecutor.executeLuckyFunction(world, player, pos, randomFunction);
                    }
                }
            }
        }
    }
}
