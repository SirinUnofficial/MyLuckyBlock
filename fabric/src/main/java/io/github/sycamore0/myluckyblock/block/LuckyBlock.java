package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class LuckyBlock extends Block {
    private String modId = MyLuckyBlock.MOD_ID; // path: data/myluckyblock/lucky_events/%modId%/
    private boolean includeBuiltIn = false; // if include built-in lucky events(include lucky_events/my_lucky_block/)

    public LuckyBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String modId) {
        super(settings);
        this.modId = modId;
        this.includeBuiltIn = false;
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String modId, boolean includeBuiltIn) {
        super(settings);
        this.modId = modId;
        this.includeBuiltIn = includeBuiltIn;
    }

    public String getModId() {
        return modId;
    }

    public boolean includeBuiltIn() {
        return includeBuiltIn;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        if (level.hasNeighborSignal(pos)) {
            Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64, null);
            if (player != null) {
                if (player.isSpectator()) {
                    return;
                }
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                BreakLuckyBlock.breakLuckyBlock(level, player, pos, state);
            }
        }
    }
}
