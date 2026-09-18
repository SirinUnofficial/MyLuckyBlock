package io.github.sycamore0.myluckyblock.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MyLuckyBlockCommand.register(dispatcher);
        });
    }
}
