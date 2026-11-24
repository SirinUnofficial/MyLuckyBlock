package io.github.sycamore0.myluckyblock.lucky;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.reader.EventDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LuckyEventDispatcher {
    public static void executeLuckyFunction(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, EventDefinition function) {
        ActionContext context = new ActionContext(serverLevel, player, blockPos);
        try {
            runSteps(context, function);
        } catch (Exception e) {
            Constants.LOG.error("Event {} execution aborted due to fatal error {}", function.getEventId(), e);
        }
    }

    private static void runSteps(ActionContext context, EventDefinition function) {
        List<JsonObject> steps = function.getSteps();
        if (steps.isEmpty()) {
            return;
        }
        for (int i = 0; i < steps.size(); i++) {
            JsonObject step = steps.get(i);
            if (step == null) continue;

            JsonElement actionElement = step.get("action");
            if (actionElement == null || !actionElement.isJsonPrimitive()) {
                Constants.LOG.warn("Event {} step #{} has no 'action', skipping", function.getEventId(), i + 1);
                continue;
            }

            String actionType = actionElement.getAsString();
            LuckyAction luckyAction = LuckyActions.get(actionType);
            if (luckyAction == null) {
                Constants.LOG.warn("Event {} step #{} uses unknown action '{}', skipping", function.getEventId(), i + 1, actionType);
                continue;
            }

            try {
                luckyAction.execute(context, step);
            } catch (Exception e) {
                Constants.LOG.error("Event {} step #{} ('{}') failed: {}", function.getEventId(), i + 1, actionType, e.toString());
            }
        }
    }
}
