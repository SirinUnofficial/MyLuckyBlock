package io.github.sycamore0.myluckyblock.lucky.addon;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AddonHandshake {
    public enum Result {
        MATCH,
        EXTRA_ON_CLIENT,
        MISSING_ON_CLIENT,
        CONTENT_MISMATCH
    }

    private AddonHandshake() {}

    public static Result compare(AddonManifest server, AddonManifest client) {
        List<AddonManifest.Entry> serverEntries = server.entries();
        List<AddonManifest.Entry> clientEntries = client.entries();
        if (serverEntries.equals(clientEntries)) return Result.MATCH;

        int serverSize = serverEntries.size();
        int clientSize = clientEntries.size();
        if (clientSize > serverSize) {
            return serverEntries.equals(clientEntries.subList(0, serverSize))
                    ? Result.EXTRA_ON_CLIENT
                    : Result.CONTENT_MISMATCH;
        }
        if (clientSize < serverSize) {
            return clientEntries.equals(serverEntries.subList(0, clientSize))
                    ? Result.MISSING_ON_CLIENT
                    : Result.CONTENT_MISMATCH;
        }
        return Result.CONTENT_MISMATCH;
    }

    // server yes, client no
    public static List<String> missingFiles(AddonManifest server, AddonManifest client) {
        int from = Math.min(server.entries().size(), client.entries().size());
        List<String> names = new ArrayList<>();
        for (int i = from; i < server.entries().size(); i++) {
            names.add(server.entries().get(i).fileName());
        }
        return names;
    }

    // server no, client yes
    public static List<String> extraFiles(AddonManifest server, AddonManifest client) {
        int from = Math.min(server.entries().size(), client.entries().size());
        List<String> names = new ArrayList<>();
        for (int i = from; i < client.entries().size(); i++) {
            names.add(client.entries().get(i).fileName());
        }
        return names;
    }

    // server yes, client yes, but content mismatch
    public static List<String> mismatchedFiles(AddonManifest server, AddonManifest client) {
        int limit = Math.min(server.entries().size(), client.entries().size());
        List<String> names = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            AddonManifest.Entry serverEntry = server.entries().get(i);
            AddonManifest.Entry clientEntry = client.entries().get(i);
            if (!serverEntry.equals(clientEntry)) {
                names.add(clientEntry.fileName() + " / " + serverEntry.fileName());
            }
        }
        return names;
    }

    public static List<String> disabledBlockIds(AddonManifest server, AddonManifest client) {
        Set<String> ids = new LinkedHashSet<>();
        int from = Math.min(server.entries().size(), client.entries().size());
        for (int i = from; i < client.entries().size(); i++) {
            ids.addAll(client.entries().get(i).blockIds());
        }
        return List.copyOf(ids);
    }

    public static String describe(AddonManifest server, AddonManifest client, Result result) {
        List<String> names = switch (result) {
            case MATCH -> List.of();
            case EXTRA_ON_CLIENT -> extraFiles(server, client);
            case MISSING_ON_CLIENT -> missingFiles(server, client);
            case CONTENT_MISMATCH -> mismatchedFiles(server, client);
        };
        return names.isEmpty() ? "?" : String.join(", ", names);
    }

    public static Component message(Result result, String detail) {
        String key = switch (result) {
            case EXTRA_ON_CLIENT -> "message.myluckyblock.addon.extra";
            case MISSING_ON_CLIENT -> "message.myluckyblock.addon.missing";
            default -> "message.myluckyblock.addon.mismatch";
        };
        return Component.translatable(key, detail);
    }
}
