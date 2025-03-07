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
    private String modId = Constants.MOD_ID; // path: data/myluckyblock/lucky_events/%modId%/
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
