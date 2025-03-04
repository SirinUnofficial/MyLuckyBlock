package io.github.sycamore0.myluckyblock.utils.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.Nullable;

public class NbtHelper {
    // generate NBT from string
    public static CompoundTag generateNbt(@Nullable String nbtStr) {
        try {
            if (nbtStr != null) {
                CompoundTag nbt = TagParser.parseTag(nbtStr);
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

    public static CompoundTag generateItemNbt(String itemId, int count, @Nullable String itemNbtStr) {
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
}