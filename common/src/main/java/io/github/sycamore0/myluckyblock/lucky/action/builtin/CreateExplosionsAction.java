package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.CreateExplosionSpec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public final class CreateExplosionsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (CreateExplosionSpec explosion : LuckyActions.spec(step, CreateExplosionSpec.class)) {
            explode(context.level(), context.resolve(explosion.getPos()), explosion.getPower(), explosion.isCreateFire());
        }
    }

    private static void explode(ServerLevel serverLevel, Vec3 pos, float power, boolean createFire) {
        serverLevel.explode(null, pos.x(), pos.y(), pos.z(), power, createFire, ServerLevel.ExplosionInteraction.BLOCK);
    }
}
