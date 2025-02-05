package io.github.sycamore0.myluckyblock.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import org.jetbrains.annotations.Nullable;

public class NbtHelper {
    // generate NBT from string
    public static NbtCompound generateNbt(@Nullable String string) {
        try {
            if (string != null) {
                NbtCompound nbt = StringNbtReader.parse(string);
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
}