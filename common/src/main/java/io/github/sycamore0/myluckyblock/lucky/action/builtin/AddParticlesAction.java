package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.AddParticle;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public final class AddParticlesAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (AddParticle particle : LuckyActions.payload(step, AddParticle.class)) {
            addParticles(context.level(), particle.getId(),
                    context.resolvePos(particle.getPosSrc(), particle.getOffset()),
                    particle.getCount(),
                    particle.getDelta().getX(), particle.getDelta().getY(), particle.getDelta().getZ(),
                    particle.getSpeed());
        }
    }

    private static void addParticles(ServerLevel serverLevel, String particleSpec, Vec3 pos, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        try {
            // placeholder for xyz coordinate
            String formattedParticleSpec = particleSpec
                    .replace("%pos_x%", Double.toString(pos.x()))
                    .replace("%pos_y%", Double.toString(pos.y()))
                    .replace("%pos_z%", Double.toString(pos.z()));
            ParticleOptions particleOptions = NbtHelper.parseParticleOptions(serverLevel.registryAccess(), formattedParticleSpec);
            serverLevel.sendParticles(particleOptions, pos.x, pos.y, pos.z, count, deltaX, deltaY, deltaZ, speed);
        } catch (CommandSyntaxException e) {
            Constants.LOG.error("Invalid particle specification '{}': {}", particleSpec, e.getMessage());
        } catch (Exception e) {
            Constants.LOG.error("Failed to send particles: {}", e.getMessage());
        }
    }
}
