package io.github.sycamore0.myluckyblock.pack;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.function.UnaryOperator;

public class ModPacks {
    @SubscribeEvent
    private static void setupDataPack(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            addBuiltInDataPack(event, "disable_lucky_block_worldgen");
            addBuiltInDataPack(event, "disable_lucky_block_structure");
        }
    }

    public static void addBuiltInDataPack(AddPackFindersEvent event, String packName) {
        event.addPackFinders(
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "datapacks/" + packName),
                PackType.SERVER_DATA,
                Component.translatable("pack.name." + packName),
                PackSource.create(decorateWithSource("pack.source.builtin"), false),
                false,
                Pack.Position.TOP
        );
    }

    // net.minecraft.server.packs.repository.PackSource#decorateWithSource(String)
    private static UnaryOperator<Component> decorateWithSource(String translationKey) {
        Component component = Component.translatable(translationKey);
        return (_component) -> Component.translatable("pack.nameAndSource", _component, component).withStyle(ChatFormatting.GRAY);
    }

    public static void onInitialize(IEventBus eventBus) {
        eventBus.addListener(ModPacks::setupDataPack);
    }
}
