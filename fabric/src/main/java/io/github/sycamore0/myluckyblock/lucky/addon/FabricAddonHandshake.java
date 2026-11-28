package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.AddonCheckPolicy;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import io.github.sycamore0.myluckyblock.network.AddonAckPayload;
import io.github.sycamore0.myluckyblock.network.AddonManifestPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class FabricAddonHandshake {
    private static volatile AddonManifest serverManifest;

    private FabricAddonHandshake() {
    }

    public static AddonManifest serverManifest() {
        AddonManifest manifest = serverManifest;
        if (manifest == null) {
            manifest = AddonManifest.local();
            serverManifest = manifest;
        }
        return manifest;
    }

    public static void onInitialize() {
        PayloadTypeRegistry.playS2C().register(AddonManifestPayload.TYPE, AddonManifestPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(AddonAckPayload.TYPE, AddonAckPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(AddonAckPayload.TYPE, (payload, context) -> handleAck(context.player(), payload.manifest()));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPlayNetworking.send(handler.getPlayer(), new AddonManifestPayload(serverManifest())));
    }

    private static void handleAck(ServerPlayer player, AddonManifest client) {
        AddonCheckPolicy policy = ConfigManager.get().addonCheck;
        if (policy == AddonCheckPolicy.OFF) return;

        AddonManifest server = serverManifest();
        AddonHandshake.Result result = AddonHandshake.compare(server, client);
        if (result == AddonHandshake.Result.MATCH) {
            Constants.LOG.debug("[Addon] {} matches the server manifest", player.getGameProfile().getName());
            return;
        }

        String detail = AddonHandshake.describe(server, client, result);
        if (policy == AddonCheckPolicy.KICK) {
            Constants.LOG.warn("[Addon] Kicking {}: {} ({})", player.getGameProfile().getName(), result, detail);
            player.connection.disconnect(AddonHandshake.message(result, detail));
        } else {
            Constants.LOG.warn("[Addon] {} differs from the server: {} ({})", player.getGameProfile().getName(), result, detail);
        }
    }

}
