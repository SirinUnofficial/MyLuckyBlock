package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.ActionSupport;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.PlaceBlockSpec;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class PlaceBlocksAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (PlaceBlockSpec block : LuckyActions.spec(step, PlaceBlockSpec.class)) {
            placeBlock(context.level(), context.resolvePos(block.getPosSrc(), block.getOffset()), block.getId(), block.getBlockState(), block.getNbt(), block.getDataComponents());
        }
    }

    private static void placeBlock(ServerLevel serverLevel, Vec3 pos, String blockId, @Nullable JsonElement blockStateElement, @Nullable String nbtString, @Nullable JsonObject dataComponentsElement) {
        BlockState blockState = ActionSupport.resolveBlockState(blockId, blockStateElement);
        if (blockState == null) {
            return;
        }
        BlockPos blockPos = PosHelper.parseVec3d(pos);
        serverLevel.setBlockAndUpdate(blockPos, blockState);
        ActionSupport.applyBlockEntityData(serverLevel, blockPos, blockId, nbtString, dataComponentsElement);
    }
}
