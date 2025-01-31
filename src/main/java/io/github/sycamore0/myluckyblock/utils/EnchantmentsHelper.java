package io.github.sycamore0.myluckyblock.utils;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Set;

public class EnchantmentsHelper {
    public static int getEnchantmentLevel(ItemStack stack, RegistryKey<Enchantment> enchantment) {
        ItemEnchantmentsComponent enchants = stack.getEnchantments();
        if (!enchants.equals(ItemEnchantmentsComponent.DEFAULT)) {
            Set<RegistryEntry<Enchantment>> enchantList = enchants.getEnchantments();
            for (RegistryEntry<Enchantment> entry : enchantList) {
                if (entry.matchesKey(enchantment)) {
                    return enchants.getLevel(entry);
                }
            }
        }
        return -1;
    }

    public static boolean checkSilkTouch(PlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        if (!mainHandStack.isEmpty() && mainHandStack.hasEnchantments()) {
            return EnchantmentsHelper.getEnchantmentLevel(mainHandStack, Enchantments.SILK_TOUCH) > 0;
        }
        return false;
    }
}
