package io.github.sycamore0.myluckyblock.utils.helper;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public class NbtHelper {
    // generate NBT from string
    public static CompoundTag generateNbt(@Nullable String nbtStr) {
        try {
            if (nbtStr != null) {
                CompoundTag nbt = TagParser.parseCompoundFully(nbtStr);
                if (nbt instanceof CompoundTag) {
                    return nbt;
                } else {
                    throw new IllegalArgumentException("Parsed NBT data is not a compound tag");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse NBT data: " + e.getMessage(), e);
        }
        return null;
    }

    @Deprecated
    public static CompoundTag generateItemEntityNbt(String itemId, int count, @Nullable String itemNbtStr) {
        if (itemNbtStr != null) {
            String nbtStr = "{Item:"
                    + "{id:\""
                    + itemId
                    + "\",count:"
                    + count
                    + ","
                    + "components:{"
                    + itemNbtStr
                    + "}}}";
            return generateNbt(nbtStr);
        }
        return null;
    }

    public static ItemStack createItemStackWithNBT(ItemStack itemStack, String nbtStr, RegistryAccess registryAccess) {
        if (nbtStr == null || nbtStr.isEmpty()) {
            return itemStack;
        }

        try {
            CompoundTag nbt = TagParser.parseCompoundFully(nbtStr);
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
            DataComponentPatch patch = DataComponentPatch.CODEC
                    .parse(registryOps, nbt)
                    .result()
                    .orElse(DataComponentPatch.EMPTY);

            if (!patch.equals(DataComponentPatch.EMPTY)) {
                itemStack.applyComponents(patch);
            } else {
                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }

        } catch (CommandSyntaxException e) {
            Constants.LOG.error("Failed to parse SNBT syntax: {}", nbtStr);
        }

        return itemStack;
    }
}