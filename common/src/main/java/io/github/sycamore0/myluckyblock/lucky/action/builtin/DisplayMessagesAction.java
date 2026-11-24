package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.DisplayMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class DisplayMessagesAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        Player player = context.player();
        if (player == null) {
            return;
        }
        for (DisplayMessage message : LuckyActions.spec(step, DisplayMessage.class)) {
            player.displayClientMessage(Component.translatableEscape(message.getMsg()), message.getOverlay());
        }
    }
}
