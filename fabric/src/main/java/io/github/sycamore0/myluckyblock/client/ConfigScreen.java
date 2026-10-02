package io.github.sycamore0.myluckyblock.client;

import io.github.sycamore0.myluckyblock.config.ConfigManager;
import io.github.sycamore0.myluckyblock.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen {
    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        ModConfig config = ConfigManager.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("MyLuckyBlock"))
                .setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder entry = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entry
                .startBooleanToggle(Component.literal("Enable Creative Trigger"), config.enableCreativeTrigger)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Enable creative player to trigger events."))
                .setSaveConsumer(v -> config.enableCreativeTrigger = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.literal("Auto Disable Lucky Block Worldgen"), config.autoDisableLuckyBlockWorldgen)
                .setDefaultValue(false)
                .setTooltip(Component.literal("§e(Require Restart)§r Auto add a built-in datapack to disable myluckyblock worldgen for new world.")) // Require Restart in Fabric
                .setSaveConsumer(v -> config.autoDisableLuckyBlockWorldgen = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.literal("Auto Disable Lucky Block Structure"), config.autoDisableLuckyBlockStructure)
                .setDefaultValue(false)
                .setTooltip(Component.literal("§e(Require Restart)§r Auto add a built-in datapack to disable myluckyblock structure events for new world."))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockStructure = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.literal("Auto Disable Lucky Block Boss"), config.autoDisableLuckyBlockBoss)
                .setDefaultValue(false)
                .setTooltip(Component.literal("§e(Require Restart)§r Auto add a built-in datapack to disable myluckyblock boss events for new world."))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockBoss = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.literal("Auto Disable Lucky Block Explosions"), config.autoDisableLuckyBlockExplosions)
                .setDefaultValue(false)
                .setTooltip(Component.literal("§e(Require Restart)§r Auto add a built-in datapack to disable myluckyblock huge explosion events for new world."))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockExplosions = v)
                .build());

        return builder.build();
    }
}