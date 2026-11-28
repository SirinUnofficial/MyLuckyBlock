package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.ActionSupport;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.FallBlockSpec;
import io.github.sycamore0.myluckyblock.utils.helper.DataComponentsHelper;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class FallBlocksAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (FallBlockSpec block : LuckyActions.spec(step, FallBlockSpec.class)) {
            fallBlock(context.level(), PosHelper.parseVec3d(context.resolve(block.getPos())), block.getId(), block.getBlockState(), block.getNbt(), block.getDataComponents(), block.getVelocity());
        }
    }

    private static void fallBlock(ServerLevel serverLevel, BlockPos blockPos, String blockId, @Nullable JsonElement blockStateElement, @Nullable String nbtString, @Nullable JsonObject dataComponentsElement, Vec3 velocity) {
        BlockState blockState = ActionSupport.resolveBlockState(blockId, blockStateElement);
        if (blockState == null) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverLevel, blockPos, blockState);
        CompoundTag blockData = DataComponentsHelper.toBlockEntityTag(serverLevel.registryAccess(), blockState.getBlock().asItem(), blockId, nbtString, dataComponentsElement);
        if (blockData != null) {
            fallingBlockEntity.blockData = blockData;
        }
        fallingBlockEntity.push(velocity);
    }
}
