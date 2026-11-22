package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.FallBlock;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class FallBlocksAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (FallBlock block : LuckyActions.payload(step, FallBlock.class)) {
            fallBlock(context.level(), PosHelper.parseVec3d(context.resolvePos(block.getPosSrc(), block.getOffset())), block.getId(), block.getVelocity());
        }
    }

    private static void fallBlock(ServerLevel serverLevel, BlockPos blockPos, String blockId, Vec3 velocity) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        BlockState blockState = block.defaultBlockState();
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverLevel, blockPos, blockState);
        fallingBlockEntity.push(velocity);
    }
}
