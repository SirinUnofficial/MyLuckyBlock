package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.GivePotionEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public final class GivePotionEffectsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        Player player = context.player();
        if (player == null) {
            return;
        }
        for (GivePotionEffect effect : LuckyActions.spec(step, GivePotionEffect.class)) {
            givePotionEffect(player, effect.getId(), effect.getDuration(), effect.getAmplifier());
        }
    }

    private static void givePotionEffect(Player player, String effectId, int duration, int amplifier) {
        Optional<Holder.Reference<MobEffect>> effectOptional = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId));
        if (effectOptional.isPresent()) {
            Holder.Reference<MobEffect> effect = effectOptional.get();
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
        }
    }
}
