package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.PlaySoundSpec;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class PlaySoundsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        Player player = context.player();
        if (player == null) {
            return;
        }
        for (PlaySoundSpec sound : LuckyActions.spec(step, PlaySoundSpec.class)) {
            playSound(player, context.level(), context.resolve(sound.getPos()), sound.getId(), sound.getVolume(), sound.getPitch());
        }
    }

    private static void playSound(Entity entity, ServerLevel serverLevel, Vec3 pos, String soundId, float volume, float pitch) {
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(soundId));
        if (soundEvent != null) {
            serverLevel.playSound(entity, PosHelper.parseVec3d(pos), soundEvent, SoundSource.BLOCKS, volume, pitch);
        } else {
            Constants.LOG.error("soundEvent is null! soundId: {}.", soundId);
        }
    }
}
