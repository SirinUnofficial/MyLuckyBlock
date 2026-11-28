package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.PlaySoundSpec;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;

public final class PlaySoundsAction implements LuckyAction {
    private static final String TARGET_WORLD = "world";
    private static final String TARGET_PLAYER = "player";

    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (PlaySoundSpec sound : LuckyActions.spec(step, PlaySoundSpec.class)) {
            playSound(context, sound);
        }
    }

    @Override
    public void validate(JsonObject step) {
        for (PlaySoundSpec spec : LuckyActions.spec(step, PlaySoundSpec.class)) {
            String soundId = spec.getId();
            ResourceLocation key = soundId == null ? null : ResourceLocation.tryParse(soundId);
            if (key == null || !BuiltInRegistries.SOUND_EVENT.containsKey(key)) {
                Constants.LOG.warn("Unknown sound id '{}' in 'play_sounds'.", soundId);
            }
            if (!TARGET_PLAYER.equals(spec.getTarget()) && !TARGET_WORLD.equals(spec.getTarget())) {
                Constants.LOG.warn("Unknown 'play_sounds' target '{}'.", spec.getTarget());
            }
        }
    }

    private static void playSound(ActionContext context, PlaySoundSpec spec) {
        String soundId = spec.getId();
        if (soundId == null || soundId.isEmpty()) {
            Constants.LOG.error("Missing sound id, skipping 'play_sounds' entry.");
            return;
        }

        ResourceLocation key = ResourceLocation.tryParse(soundId);
        if (key == null || !BuiltInRegistries.SOUND_EVENT.containsKey(key)) {
            Constants.LOG.error("Unknown sound id '{}', skipping 'play_sounds' entry.", soundId);
            return;
        }
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(key);

        SoundSource source = parseSource(spec.getSource());
        ServerLevel level = context.level();
        Vec3 pos = spec.getPos().isDefault() ? null : context.resolve(spec.getPos());
        String target = spec.getTarget();

        if (TARGET_WORLD.equals(target)) {
            Vec3 at = pos == null ? PosHelper.parseBlockPos(context.pos()) : pos;
            level.playSound(null, at.x, at.y, at.z, soundEvent, source, spec.getVolume(), spec.getPitch());
            return;
        }

        if (!TARGET_PLAYER.equals(target)) {
            Constants.LOG.warn("Unknown 'play_sounds' target '{}', using '{}'.", target, TARGET_PLAYER);
        }

        Player player = context.player();
        if (player == null) {
            Constants.LOG.debug("Skipping 'play_sounds' for a missing player, soundId: {}.", soundId);
            return;
        }

        if (pos == null) {
            player.playNotifySound(soundEvent, source, spec.getVolume(), spec.getPitch());
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent),
                    source, pos.x, pos.y, pos.z,
                    spec.getVolume(), spec.getPitch(), level.getRandom().nextLong()));
        }
    }

    private static SoundSource parseSource(String name) {
        String normalized = name.trim().toUpperCase(Locale.ROOT);
        for (String candidate : new String[]{normalized, normalized + "S", normalized.endsWith("S") ? normalized.substring(0, normalized.length() - 1) : normalized}) {
            try {
                return SoundSource.valueOf(candidate);
            } catch (IllegalArgumentException ignored) {
            }
        }
        Constants.LOG.warn("Unknown sound source '{}', using BLOCKS.", name);
        return SoundSource.BLOCKS;
    }
}
