package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.utils.BreakLuckyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LuckyBlock extends Block {
    private final String modId;
    private final boolean includeBuiltIn;

    public LuckyBlock(Settings settings, String modId, boolean includeBuiltIn) {
        super(settings);
        this.modId = modId; // path: data/myluckyblock/lucky_events/%modId%/
        this.includeBuiltIn = includeBuiltIn; // include built-in lucky events
    }

    public String getModId() {
        return modId;
    }

    public boolean includeBuiltIn() {
        return includeBuiltIn;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (world.isReceivingRedstonePower(pos)) {
            PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64, null);
            if (player != null) {
                if (player.isSpectator()) {
                    return;
                }
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                BreakLuckyBlock.breakLuckyBlock(world, player, pos, state);
            }
        }
    }
}
