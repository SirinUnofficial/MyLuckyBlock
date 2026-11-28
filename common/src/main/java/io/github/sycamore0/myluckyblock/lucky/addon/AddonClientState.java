package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AddonClientState {
    private static volatile Set<ResourceLocation> disabledBlocks = Set.of();
    private static volatile boolean verified;

    private AddonClientState() {
    }

    public static void update(List<String> disabledIds) {
        Set<ResourceLocation> ids = new LinkedHashSet<>();
        for (String id : disabledIds) {
            ResourceLocation location = ResourceLocation.tryParse(id);
            if (location != null) ids.add(location);
        }
        disabledBlocks = Set.copyOf(ids);
        verified = true;
        if (!ids.isEmpty()) {
            List<String> names = new ArrayList<>();
            ids.forEach(location -> names.add(location.toString()));
            Constants.LOG.info("[Addon] {} block(s) are not available on this server: {}",
                    names.size(), String.join(", ", names));
        }
    }

    public static boolean isVerified() {
        return verified;
    }

    public static boolean isDisabled(Block block) {
        if (disabledBlocks.isEmpty()) return false;
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        return blockId != null && disabledBlocks.contains(blockId);
    }

    public static boolean isDisabled(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof BlockItem blockItem
                && isDisabled(blockItem.getBlock());
    }
}
