package io.github.sycamore0.myluckyblock.pack;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipPackResources extends AbstractPackResources {
    private final ZipFile zip;
    private final String rootPrefix;
    private final Map<String, ZipEntry> entries;
    private final Map<PackType, Set<String>> namespaces;

    private ZipPackResources(PackLocationInfo location, ZipFile zip, String rootPrefix) {
        super(location);
        this.zip = zip;
        this.rootPrefix = rootPrefix;
        this.entries = indexEntries(zip);
        this.namespaces = collectNamespaces();
    }

    public static ZipPackResources open(Path archive, String rootPrefix, PackLocationInfo location) throws IOException {
        ZipFile zip = new ZipFile(archive.toFile());
        try {
            return new ZipPackResources(location, zip, rootPrefix);
        } catch (RuntimeException e) {
            zip.close();
            throw e;
        }
    }

    private static Map<String, ZipEntry> indexEntries(ZipFile zip) {
        Map<String, ZipEntry> zipIndex = new LinkedHashMap<>();
        Enumeration<? extends ZipEntry> zipEntriesEnum = zip.entries();
        while (zipEntriesEnum.hasMoreElements()) {
            ZipEntry zipEntry = zipEntriesEnum.nextElement();
            if (!zipEntry.isDirectory()) {
                zipIndex.put(zipEntry.getName(), zipEntry);
            }
        }
        return Collections.unmodifiableMap(zipIndex);
    }

    private Map<PackType, Set<String>> collectNamespaces() {
        Map<PackType, Set<String>> result = new EnumMap<>(PackType.class);
        for (PackType type : PackType.values()) {
            String directory = rootPrefix + type.getDirectory() + "/";
            Set<String> found = new LinkedHashSet<>();
            for (String name : entries.keySet()) {
                if (!name.startsWith(directory)) continue;
                int end = name.indexOf('/', directory.length());
                if (end > directory.length()) {
                    found.add(name.substring(directory.length(), end));
                }
            }
            result.put(type, Collections.unmodifiableSet(found));
        }
        return Collections.unmodifiableMap(result);
    }

    private IoSupplier<InputStream> lookup(String relativePath) {
        ZipEntry zipEntry = entries.get(rootPrefix + relativePath);
        return zipEntry == null ? null : IoSupplier.create(zip, zipEntry);
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String @NotNull ... path) {
        return lookup(String.join("/", path));
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        return lookup(type.getDirectory() + "/" + location.getNamespace() + "/" + location.getPath());
    }

    @Override
    public void listResources(PackType type, @NotNull String namespace, @NotNull String path, @NotNull ResourceOutput output) {
        String namespacePrefix = rootPrefix + type.getDirectory() + "/" + namespace + "/";
        String filter = path == null || path.isEmpty() ? namespacePrefix : namespacePrefix + path + "/";
        for (Map.Entry<String, ZipEntry> entry : entries.entrySet()) {
            String name = entry.getKey();
            if (!name.startsWith(filter)) continue;

            ResourceLocation location = ResourceLocation.tryBuild(namespace, name.substring(namespacePrefix.length()));
            if (location == null) {
                Constants.LOG.warn("[Addon] Skipping invalid resource path {}", name);
                continue;
            }
            output.accept(location, IoSupplier.create(zip, entry.getValue()));
        }
    }

    @Override
    public @NotNull Set<String> getNamespaces(@NotNull PackType type) {
        return namespaces.getOrDefault(type, Set.of());
    }

    @Override
    public void close() {
        try {
            zip.close();
        } catch (IOException e) {
            Constants.LOG.debug("[Addon] Failed to close archive {}", zip.getName(), e);
        }
    }

    public record Supplier(Path archive, String rootPrefix) implements Pack.ResourcesSupplier {
        @Override
        public @NotNull PackResources openPrimary(@NotNull PackLocationInfo location) {
            return open(location);
        }

        @Override
        public @NotNull PackResources openFull(@NotNull PackLocationInfo location, Pack.@NotNull Metadata metadata) {
            return open(location);
        }

        private PackResources open(PackLocationInfo location) {
            try {
                return ZipPackResources.open(archive, rootPrefix, location);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to open addon archive " + archive, e);
            }
        }
    }
}
