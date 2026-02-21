package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class LuckyBlock extends Block {
    private String eventPackGroupName = Constants.EVENT_PACK_GROUP_NAME;
    private boolean includeBuiltin = false;

    public LuckyBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String eventPackGroupName) {
        super(settings);
        this.eventPackGroupName = eventPackGroupName;
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String eventPackGroupName, boolean includeBuiltIn) {
        super(settings);
        this.eventPackGroupName = eventPackGroupName;
        this.includeBuiltin = includeBuiltIn;
    }

    public String getEventPackGroupName() {
        return eventPackGroupName;
    }

    public boolean isIncludeBuiltIn() {
        return includeBuiltin;
    }

    @Override
    protected void neighborChanged(@NotNull BlockState blockState, Level level, @NotNull BlockPos blockPos, @NotNull Block sourceBlock, @NotNull BlockPos sourceBlockPos, boolean notify) {
        if (level.hasNeighborSignal(blockPos)) {
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            BreakLuckyBlock.breakLuckyBlock(level, null, blockPos, blockState);
        }
    }
}
