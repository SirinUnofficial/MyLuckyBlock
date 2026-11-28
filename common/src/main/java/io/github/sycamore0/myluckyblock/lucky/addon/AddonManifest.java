package io.github.sycamore0.myluckyblock.lucky.addon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.platform.Services;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public record AddonManifest(int protocolVersion, List<Entry> entries) {
    public static final int PROTOCOL_VERSION = 1;
    public static final AddonManifest EMPTY = new AddonManifest(PROTOCOL_VERSION, List.of());

    public record Entry(String fileName, String sha1, List<String> blockIds) {
        public Entry {
            blockIds = List.copyOf(blockIds);
        }
    }

    public AddonManifest {
        entries = List.copyOf(entries);
    }

    public static AddonManifest local() {
        return create(AddonSource.scan(Services.PLATFORM.getConfigDir().resolve(Constants.ADDONS_PATH)));
    }

    public static AddonManifest create(List<AddonSource> sources) {
        List<Entry> entries = new ArrayList<>(sources.size());
        for (AddonSource source : sources) {
            List<String> ids = new ArrayList<>(source.definitions().size());
            source.definitions().forEach(definition -> ids.add(definition.id()));
            entries.add(new Entry(source.fileName(), sha1(source.path()), ids));
        }
        return new AddonManifest(PROTOCOL_VERSION, entries);
    }

    public String fingerprint() {
        return sha1(toJsonString().getBytes(StandardCharsets.UTF_8));
    }

    public String toJsonString() {
        return Constants.GSON.toJson(toJson());
    }

    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("protocol", protocolVersion);
        JsonArray array = new JsonArray();
        for (Entry entry : entries) {
            JsonObject json = new JsonObject();
            json.addProperty("file", entry.fileName());
            json.addProperty("sha1", entry.sha1());
            JsonArray ids = new JsonArray();
            entry.blockIds().forEach(ids::add);
            json.add("blocks", ids);
            array.add(json);
        }
        root.add("entries", array);
        return root;
    }

    public static AddonManifest fromJsonString(String json) {
        try {
            JsonObject root = Constants.GSON.fromJson(json, JsonObject.class);
            return root == null ? EMPTY : fromJson(root);
        } catch (RuntimeException e) {
            Constants.LOG.warn("[Addon] Failed to parse addon manifest: {}", e.getMessage());
            return EMPTY;
        }
    }

    public static AddonManifest fromJson(JsonObject root) {
        int protocol = root.has("protocol") && !root.get("protocol").isJsonNull() ? root.get("protocol").getAsInt() : 0;
        List<Entry> entries = new ArrayList<>();
        JsonArray array = root.getAsJsonArray("entries");
        if (array != null) {
            for (JsonElement element : array) {
                if (!element.isJsonObject()) continue;
                JsonObject json = element.getAsJsonObject();
                List<String> ids = new ArrayList<>();
                JsonArray blocks = json.getAsJsonArray("blocks");
                if (blocks != null) {
                    for (JsonElement block : blocks) {
                        if (block.isJsonPrimitive()) ids.add(block.getAsString());
                    }
                }
                entries.add(new Entry(string(json, "file"), string(json, "sha1"), ids));
            }
        }
        return new AddonManifest(protocol, entries);
    }

    private static String string(JsonObject json, String key) {
        JsonElement element = json.get(key);
        return element == null || element.isJsonNull() ? "" : element.getAsString();
    }

    private static String sha1(Path path) {
        try {
            return sha1(Files.readAllBytes(path));
        } catch (Exception e) {
            Constants.LOG.warn("[Addon] Failed to hash {}: {}", path, e.getMessage());
            return "";
        }
    }

    private static String sha1(byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(data));
        } catch (Exception e) {
            Constants.LOG.warn("[Addon] Failed to hash addon manifest: {}", e.getMessage());
            return "";
        }
    }
}
