package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.AddonCheckPolicy;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import io.github.sycamore0.myluckyblock.network.AddonAckPayload;
import io.github.sycamore0.myluckyblock.network.AddonManifestPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NeoForgeAddonHandshake {
    private static volatile AddonManifest serverManifest;

    private NeoForgeAddonHandshake() {
    }

    public static AddonManifest serverManifest() {
        AddonManifest manifest = serverManifest;
        if (manifest == null) {
            manifest = AddonManifest.local();
            serverManifest = manifest;
        }
        return manifest;
    }

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(AddonManifestPayload.TYPE, AddonManifestPayload.STREAM_CODEC, (payload, context) -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                context.enqueueWork(() -> AddonClientHooks.handle(payload.manifest()));
            }
        });
        registrar.playToServer(AddonAckPayload.TYPE, AddonAckPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> handleAck(context.player(), payload.manifest())));
    }

    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new AddonManifestPayload(serverManifest()));
        }
    }

    private static void handleAck(net.minecraft.world.entity.player.Player sender, AddonManifest client) {
        if (!(sender instanceof ServerPlayer player)) return;

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
