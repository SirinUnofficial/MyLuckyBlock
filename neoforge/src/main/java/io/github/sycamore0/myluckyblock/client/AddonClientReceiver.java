package io.github.sycamore0.myluckyblock.client;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.addon.AddonClientHooks;
import io.github.sycamore0.myluckyblock.lucky.addon.AddonClientState;
import io.github.sycamore0.myluckyblock.lucky.addon.AddonHandshake;
import io.github.sycamore0.myluckyblock.lucky.addon.AddonManifest;
import io.github.sycamore0.myluckyblock.network.AddonAckPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class AddonClientReceiver {
    private AddonClientReceiver() {
    }

    public static void onInitializeClient() {
        AddonClientHooks.set(AddonClientReceiver::receive);
    }

    private static void receive(AddonManifest server) {
        AddonManifest local = AddonManifest.local();
        AddonHandshake.Result result = AddonHandshake.compare(server, local);

        if (result == AddonHandshake.Result.MATCH) {
            AddonClientState.update(List.of());
        } else {
            AddonClientState.update(AddonHandshake.disabledBlockIds(server, local));
        }

        if (result != AddonHandshake.Result.MATCH) {
            Minecraft.getInstance().gui.getChat().addMessage(AddonHandshake.message(result, AddonHandshake.describe(server, local, result)).copy().withStyle(ChatFormatting.YELLOW));
        }

        try {
            PacketDistributor.sendToServer(new AddonAckPayload(local));
        } catch (RuntimeException e) {
            Constants.LOG.debug("[Addon] Could not send the addon ack: {}", e.getMessage());
        }

        if (result == AddonHandshake.Result.MATCH || result == AddonHandshake.Result.EXTRA_ON_CLIENT) return;

        Minecraft minecraft = Minecraft.getInstance();
        Connection connection = minecraft.getConnection() == null ? null : minecraft.getConnection().getConnection();
        if (connection != null) {
            connection.disconnect(AddonHandshake.message(result, AddonHandshake.describe(server, local, result)));
        }
    }
}
