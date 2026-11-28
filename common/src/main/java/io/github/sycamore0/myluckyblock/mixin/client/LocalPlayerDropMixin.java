package io.github.sycamore0.myluckyblock.mixin.client;

import io.github.sycamore0.myluckyblock.lucky.addon.AddonClientState;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerDropMixin {
    @Inject(method = "drop(Z)Z", at = @At("HEAD"), cancellable = true)
    private void myluckyblock$preventDrop(boolean all, CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if (AddonClientState.isDisabled(player.getInventory().getSelected())) {
            cir.setReturnValue(false);
        }
    }
}
