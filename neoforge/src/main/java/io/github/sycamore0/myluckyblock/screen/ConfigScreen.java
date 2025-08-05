package io.github.sycamore0.myluckyblock.screen;

import io.github.sycamore0.myluckyblock.config.ModConfigNeo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.translatable("config.myluckyblock.title"));
        this.parent = parent;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    protected void init() {
        // base
        Button done_button = Button.builder(Component.translatable("config.myluckyblock.done"), button -> {
                    Minecraft.getInstance().setScreen(parent);
                })
                .bounds(width / 2 - 100, height - 30, 200, 20)
                .build();

        // configs
        Checkbox generate_lucky_block_checkbox = Checkbox.builder(Component.translatable("config.myluckyblock.generate_lucky_block"), Minecraft.getInstance().font)
                .pos(width / 2 - 100, height / 2 - 10)
                .maxWidth(200)
                .selected(ModConfigNeo.generate_lucky_block.get())
                .onValueChange((checkbox, selected) -> {
                    ModConfigNeo.generate_lucky_block.set(selected);
                    done_button.setTooltip(Tooltip.create(Component.translatable("config.myluckyblock.require_restart")));
                })
                .tooltip(Tooltip.create(Component.translatable("config.myluckyblock.require_restart")))
                .build();

        // base
        addRenderableWidget(done_button);

        // configs
        addRenderableWidget(generate_lucky_block_checkbox);
    }
}