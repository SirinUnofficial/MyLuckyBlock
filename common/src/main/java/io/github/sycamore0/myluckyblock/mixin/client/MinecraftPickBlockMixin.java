package io.github.sycamore0.myluckyblock.mixin.client;

import io.github.sycamore0.myluckyblock.lucky.addon.AddonClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftPickBlockMixin {
    @Shadow
    public HitResult hitResult;

    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void myluckyblock$preventPickBlock(CallbackInfo ci) {
        if (!(this.hitResult instanceof BlockHitResult hit)) {
            return;
        }
        Minecraft minecraft = (Minecraft) (Object) this;
        if (minecraft.level != null
                && AddonClientState.isDisabled(minecraft.level.getBlockState(hit.getBlockPos()).getBlock())) {
            ci.cancel();
        }
    }
}
