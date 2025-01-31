package io.github.sycamore0.myluckyblock.item;

import io.github.sycamore0.myluckyblock.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup MYLUCKYBLOCK_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.MY_LUCKY_BLOCK))
            .displayName(Text.translatable("itemGroup.myluckyblock.myluckyblock_group"))
            .entries((context, entries) -> {
                entries.add(ModBlocks.MY_LUCKY_BLOCK);
                entries.add(ModBlocks.DEBUG_LUCKY_BLOCK);
            })
            .build();

    public static void registerModItemGroups() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of("myluckyblock", "myluckyblock_group"), MYLUCKYBLOCK_GROUP);
    }

}
