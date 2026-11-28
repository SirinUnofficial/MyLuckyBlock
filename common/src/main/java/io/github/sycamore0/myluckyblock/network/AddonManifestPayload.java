package io.github.sycamore0.myluckyblock.network;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.addon.AddonManifest;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record AddonManifestPayload(AddonManifest manifest) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AddonManifestPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "addon_manifest"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddonManifestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            payload -> payload.manifest().toJsonString(),
            json -> new AddonManifestPayload(AddonManifest.fromJsonString(json)));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
