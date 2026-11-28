package io.github.sycamore0.myluckyblock.mixin.client;

import io.github.sycamore0.myluckyblock.lucky.addon.AddonClientState;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(method = "handleCreativeModeItemAdd", at = @At("HEAD"), cancellable = true)
    private void myluckyblock$preventCreativeAdd(ItemStack stack, int slot, CallbackInfo ci) {
        if (AddonClientState.isDisabled(stack)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleCreativeModeItemDrop", at = @At("HEAD"), cancellable = true)
    private void myluckyblock$preventCreativeDrop(ItemStack stack, CallbackInfo ci) {
        if (AddonClientState.isDisabled(stack)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
    private void myluckyblock$preventInventoryClick(int containerId, int slotNum, int buttonNum, ClickType clickType, Player player, CallbackInfo ci) {
        AbstractContainerMenu menu = player.containerMenu;
        if (AddonClientState.isDisabled(menu.getCarried())) {
            ci.cancel();
            return;
        }
        if (slotNum >= 0 && slotNum < menu.slots.size() && AddonClientState.isDisabled(menu.getSlot(slotNum).getItem())) {
            ci.cancel();
        }
    }
}
