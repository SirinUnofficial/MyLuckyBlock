package io.github.sycamore0.myluckyblock.lucky.addon;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public record AddonSource(Path path, String fileName, String rootPrefix, boolean hasPackMeta,
                          List<AddonLuckyBlockDefinition> definitions) {
    private static final String ZIP_EXTENSION = ".zip";
    private static final String PACK_META = "pack.mcmeta";
    private static final String LUCKY_DIR = "lucky/";

    private static final Map<Path, List<AddonSource>> CACHE = new ConcurrentHashMap<>();

    public String baseName() {
        return fileName.substring(0, fileName.length() - ZIP_EXTENSION.length());
    }

    public boolean hasDefinitions() {
        return !definitions.isEmpty();
    }

    public static List<AddonSource> scan(Path addonsDir) {
        return CACHE.computeIfAbsent(addonsDir.toAbsolutePath().normalize(), AddonSource::scanUncached);
    }

    private static List<AddonSource> scanUncached(Path addonsDir) {
        if (!Files.isDirectory(addonsDir)) {
            Constants.LOG.info("[Addon] No addons directory at {}, skipping.", addonsDir);
            return List.of();
        }

        List<Path> archives = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(addonsDir)) {
            for (Path file : stream) {
                String name = file.getFileName().toString();
                if (name.startsWith(".")) continue;
                if (!name.toLowerCase(Locale.ROOT).endsWith(ZIP_EXTENSION)) {
                    Constants.LOG.warn("[Addon] Ignoring {}: only .zip archives are supported", name);
                    continue;
                }
                if (!Files.isRegularFile(file)) {
                    Constants.LOG.warn("[Addon] Ignoring {}: not a regular file", name);
                    continue;
                }
                archives.add(file);
            }
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to scan {}", addonsDir, e);
            return List.of();
        }

        archives.sort(Comparator.comparing(file -> file.getFileName().toString()));

        Set<ResourceLocation> seenIds = new HashSet<>();
        List<AddonSource> sources = new ArrayList<>(archives.size());
        for (Path archive : archives) {
            AddonSource source = read(archive, seenIds);
            if (source != null) sources.add(source);
        }

        int definitionCount = sources.stream().mapToInt(source -> source.definitions().size()).sum();
        Constants.LOG.info("[Addon] Loaded {} archive(s) with {} block definition(s)", sources.size(), definitionCount);
        return List.copyOf(sources);
    }

    private static AddonSource read(Path archive, Set<ResourceLocation> seenIds) {
        String fileName = archive.getFileName().toString();
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            String rootPrefix = detectRootPrefix(zip);
            if (rootPrefix == null) {
                Constants.LOG.warn("[Addon] {} is not an addon archive: expected pack.mcmeta, lucky/, assets/ or data/ at the root", fileName);
                return null;
            }
            boolean hasPackMeta = zip.getEntry(rootPrefix + PACK_META) != null;
            List<AddonLuckyBlockDefinition> definitions = readDefinitions(zip, rootPrefix, fileName, seenIds);
            return new AddonSource(archive, fileName, rootPrefix, hasPackMeta, definitions);
        } catch (IOException e) {
            Constants.LOG.error("[Addon] Failed to open {}: {}", fileName, e.getMessage());
            return null;
        }
    }

    private static String detectRootPrefix(ZipFile zip) {
        boolean markerAtRoot = false;
        Set<String> wrappedDirs = new LinkedHashSet<>();

        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            String name = entry.getName();
            if (name.startsWith("/") || name.contains("..")) continue;

            if (isRootMarker(name)) {
                markerAtRoot = true;
                continue;
            }
            int slash = name.indexOf('/');
            if (slash > 0 && isRootMarker(name.substring(slash + 1))) {
                wrappedDirs.add(name.substring(0, slash));
            }
        }

        if (markerAtRoot) return "";
        if (wrappedDirs.size() == 1) return wrappedDirs.iterator().next() + "/";
        return null;
    }

    private static boolean isRootMarker(String path) {
        return path.equals(PACK_META)
                || path.startsWith(LUCKY_DIR)
                || path.startsWith("assets/")
                || path.startsWith("data/");
    }

    private static List<AddonLuckyBlockDefinition> readDefinitions(ZipFile zip, String rootPrefix, String fileName,
                                                                  Set<ResourceLocation> seenIds) {
        String prefix = rootPrefix + LUCKY_DIR;
        List<AddonLuckyBlockDefinition> definitions = new ArrayList<>();

        Enumeration<? extends ZipEntry> zipEntries = zip.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            if (zipEntry.isDirectory()) continue;

            String zipEntryName = zipEntry.getName();
            if (!zipEntryName.startsWith(prefix) || !zipEntryName.endsWith(".json")) continue;
            String relative = zipEntryName.substring(prefix.length());
            if (relative.isEmpty() || relative.indexOf('/') >= 0) continue;

            AddonLuckyBlockDefinition definition;
            try (Reader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(zipEntry), StandardCharsets.UTF_8))) {
                definition = Constants.GSON.fromJson(reader, AddonLuckyBlockDefinition.class);
            } catch (Exception e) {
                Constants.LOG.error("[Addon] Failed to parse {}:{}: {}", fileName, relative, e.getMessage());
                continue;
            }

            if (definition == null || definition.id() == null || definition.id().isBlank()) {
                Constants.LOG.warn("[Addon] Skipping invalid definition {}:{}", fileName, relative);
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(definition.id());
            if (id == null) {
                Constants.LOG.warn("[Addon] Invalid block id '{}' in {}:{}", definition.id(), fileName, relative);
                continue;
            }
            if (!seenIds.add(id)) {
                Constants.LOG.warn("[Addon] Duplicate block id '{}' in {}, ignoring", id, fileName);
                continue;
            }

            definitions.add(definition);
            Constants.LOG.info("[Addon] Parsed block {} from {}:{}", id, fileName, relative);
        }

        return List.copyOf(definitions);
    }
}
