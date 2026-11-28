package io.github.sycamore0.myluckyblock.client;

import io.github.sycamore0.myluckyblock.config.ConfigManager;
import io.github.sycamore0.myluckyblock.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen {
    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        ModConfig config = ConfigManager.get();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.myluckyblock.title"))
                .setSavingRunnable(ConfigManager::save);

        ConfigEntryBuilder entry = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.myluckyblock.category.general"));

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.enable_creative_trigger"), config.enableCreativeTrigger)
                .setDefaultValue(true)
                .setTooltip(tooltip("config.myluckyblock.enable_creative_trigger.tooltip", "enableCreativeTrigger"))
                .setSaveConsumer(v -> config.enableCreativeTrigger = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.enable_debug_command"), config.enableDebugCommand)
                .setDefaultValue(false)
                .setTooltip(tooltip("config.myluckyblock.enable_debug_command.tooltip", "enableDebugCommand"))
                .setSaveConsumer(v -> config.enableDebugCommand = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.silence_minecraft_version_skips"), config.silenceMinecraftVersionSkipWarnings)
                .setDefaultValue(true)
                .setTooltip(tooltip("config.myluckyblock.silence_minecraft_version_skips.tooltip", "silenceMinecraftVersionSkipWarnings"))
                .setSaveConsumer(v -> config.silenceMinecraftVersionSkipWarnings = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.auto_disable_lucky_block_worldgen"), config.autoDisableLuckyBlockWorldgen)
                .setDefaultValue(false)
                .setTooltip(tooltip("config.myluckyblock.auto_disable_lucky_block_worldgen.tooltip", "autoDisableLuckyBlockWorldgen"))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockWorldgen = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.auto_disable_lucky_block_structure"), config.autoDisableLuckyBlockStructure)
                .setDefaultValue(false)
                .setTooltip(tooltip("config.myluckyblock.auto_disable_lucky_block_structure.tooltip", "autoDisableLuckyBlockStructure"))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockStructure = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.auto_disable_lucky_block_boss"), config.autoDisableLuckyBlockBoss)
                .setDefaultValue(false)
                .setTooltip(tooltip("config.myluckyblock.auto_disable_lucky_block_boss.tooltip", "autoDisableLuckyBlockBoss"))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockBoss = v)
                .build());

        general.addEntry(entry
                .startBooleanToggle(Component.translatable("config.myluckyblock.auto_disable_lucky_block_explosions"), config.autoDisableLuckyBlockExplosions)
                .setDefaultValue(false)
                .setTooltip(tooltip("config.myluckyblock.auto_disable_lucky_block_explosions.tooltip", "autoDisableLuckyBlockExplosions"))
                .setSaveConsumer(v -> config.autoDisableLuckyBlockExplosions = v)
                .build());

        general.addEntry(entry
                .startIntSlider(Component.translatable("config.myluckyblock.max_passenger_depth"), config.maxPassengerDepth, 1, 32)
                .setDefaultValue(16)
                .setTooltip(tooltip("config.myluckyblock.max_passenger_depth.tooltip", "maxPassengerDepth"))
                .setSaveConsumer(v -> config.maxPassengerDepth = v)
                .build());

        return builder.build();
    }

    /** Tooltip: description on the first line, the config field name on the second. */
    private static Component[] tooltip(String key, String field) {
        return new Component[]{
                Component.translatable(key),
                Component.literal(field).withStyle(ChatFormatting.DARK_GRAY)
        };
    }
}