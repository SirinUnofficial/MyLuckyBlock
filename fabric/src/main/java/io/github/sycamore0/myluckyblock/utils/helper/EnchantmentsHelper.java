package io.github.sycamore0.myluckyblock.utils.helper;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Set;

public class EnchantmentsHelper {
    public static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        ItemEnchantments enchants = stack.getEnchantments();
        if (enchants.equals(ItemEnchantments.EMPTY)) return -1;
        Set<Holder<Enchantment>> enchantList = enchants.keySet();
        for (Holder<Enchantment> entry : enchantList) {
            if (entry.is(enchantment)) {
                return enchants.getLevel(entry);
            }
        }
        return -1;
    }

    public static boolean checkSilkTouch(Player player) {
        ItemStack mainHandStack = player.getMainHandItem();
        if (!mainHandStack.isEmpty() && mainHandStack.isEnchanted()) {
            return EnchantmentsHelper.getEnchantmentLevel(mainHandStack, Enchantments.SILK_TOUCH) > 0;
        }
        return false;
    }
}
