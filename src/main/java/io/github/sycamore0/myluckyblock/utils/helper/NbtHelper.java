package io.github.sycamore0.myluckyblock.utils.helper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import org.jetbrains.annotations.Nullable;

public class NbtHelper {
    // generate NBT from string
    public static NbtCompound generateNbt(@Nullable String nbtStr) {
        try {
            if (nbtStr != null) {
                NbtCompound nbt = StringNbtReader.parse(nbtStr);
                if (nbt instanceof NbtCompound) {
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

    public static NbtCompound generateItemNbt(String itemId, int count, @Nullable String itemNbtStr) {
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