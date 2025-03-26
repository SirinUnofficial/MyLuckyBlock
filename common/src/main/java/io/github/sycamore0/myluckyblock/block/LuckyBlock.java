package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public class LuckyBlock extends Block {
    private String eventPackId = Constants.MOD_ID; // path: data/myluckyblock/lucky/events/%eventPackId%/
    private boolean includeBuiltIn = false; // if include built-in lucky events(include lucky/events/my_lucky_block/)

    public LuckyBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String eventPackId) {
        super(settings);
        this.eventPackId = eventPackId;
        this.includeBuiltIn = false;
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String eventPackId, boolean includeBuiltIn) {
        super(settings);
        this.eventPackId = eventPackId;
        this.includeBuiltIn = includeBuiltIn;
    }

    public String getEventPackId() {
        return eventPackId;
    }

    public boolean includeBuiltIn() {
        return includeBuiltIn;
    }

    @Override
    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, @Nullable Orientation orientation, boolean bl) {
        if (level.isClientSide) {
            return;
        }
        if (level.hasNeighborSignal(blockPos)) {
            Player player = level.getNearestPlayer(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 64, null);
            if (player != null) {
                if (player.isSpectator()) {
                    return;
                }
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                BreakLuckyBlock.breakLuckyBlock(level, player, blockPos, blockState);
            }
        }
    }
}
