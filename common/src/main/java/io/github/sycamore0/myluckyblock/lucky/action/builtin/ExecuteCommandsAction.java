package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.ExecuteCommandSpec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.phys.Vec3;

public final class ExecuteCommandsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (ExecuteCommandSpec command : LuckyActions.spec(step, ExecuteCommandSpec.class)) {
            executeCommand(context.level(), context.resolve(command.getPos()), command.getCommand());
        }
    }

    private static void executeCommand(ServerLevel serverLevel, Vec3 pos, String command) {
        MinecartCommandBlock commandMinecart = new MinecartCommandBlock(serverLevel, pos.x(), pos.y(), pos.z());
        commandMinecart.setCustomName(Component.translatable(Constants.MOD_ID));
        commandMinecart.getCommandBlock().setCommand(command);
        commandMinecart.setPos(pos.x(), pos.y(), pos.z());
        serverLevel.addFreshEntity(commandMinecart);
        commandMinecart.getCommandBlock().performCommand(serverLevel);
        commandMinecart.discard();
    }
}
