package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.SendMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class SendMessagesAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        Player player = context.player();
        if (player == null) {
            return;
        }
        for (SendMessage message : LuckyActions.payload(step, SendMessage.class)) {
            player.sendSystemMessage(Component.translatableEscape(message.getMsg()));
        }
    }
}
