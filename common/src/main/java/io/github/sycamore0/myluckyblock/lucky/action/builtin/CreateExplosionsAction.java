package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.CreateExplosion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public final class CreateExplosionsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (CreateExplosion explosion : LuckyActions.spec(step, CreateExplosion.class)) {
            explode(context.level(), context.resolvePos(explosion.getPosSrc(), explosion.getOffset()), explosion.getPower(), explosion.isCreateFire());
        }
    }

    private static void explode(ServerLevel serverLevel, Vec3 pos, float power, boolean createFire) {
        serverLevel.explode(null, pos.x(), pos.y(), pos.z(), power, createFire, ServerLevel.ExplosionInteraction.BLOCK);
    }
}
