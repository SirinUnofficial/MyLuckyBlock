package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.utils.BreakLuckyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class LuckyBlock extends Block {
    private String modId = MyLuckyBlock.MOD_ID; // path: data/myluckyblock/lucky_events/%modId%/
    private boolean includeBuiltIn = false; // if include built-in lucky events(include lucky_events/my_lucky_block/)
    private Text toolTips = null; // ToolTips

    public LuckyBlock(Settings settings) {
        super(settings);
    }

    public LuckyBlock(Settings settings, String modId) {
        super(settings);
        this.modId = modId;
    }

    public LuckyBlock(Settings settings, String modId, boolean includeBuiltIn) {
        super(settings);
        this.modId = modId;
        this.includeBuiltIn = includeBuiltIn;
    }

    public LuckyBlock(Settings settings, String modId, boolean includeBuiltIn, Text tooltip) {
        super(settings);
        this.modId = modId;
        this.includeBuiltIn = includeBuiltIn;
        this.toolTips = tooltip;
    }

    public String getModId() {
        return modId;
    }

    public boolean includeBuiltIn() {
        return includeBuiltIn;
    }

    public Text getTooltip() {
        return toolTips;
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

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        tooltip.add(toolTips);
    }
}
