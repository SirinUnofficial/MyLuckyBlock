package io.github.sycamore0.myluckyblock.utils.helper;

import io.github.sycamore0.myluckyblock.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHelper {
    // 版本号正则表达式
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "^(\\d+)\\.(\\d+)\\.(\\d+)(?:-([a-zA-Z0-9.-]+))?(?:\\+([a-zA-Z0-9.-]+))?$");

    // 版本范围正则表达式
    private static final Pattern RANGE_PATTERN = Pattern.compile(
            "^(\\[|\\()([^,]+),([^\\]\\)]+)(\\]|\\))$");

    /**
     * Is version in version range
     *
     * @param versionStr version string
     * @param rangeStr   version range string
     * @return if in range return true，or return false
     */
    public static boolean isVersionInRange(String versionStr, String rangeStr) {
        try {
            Version version = parseVersion(versionStr);
            VersionRange range = parseVersionRange(rangeStr);
            return range.contains(version);
        } catch (IllegalArgumentException e) {
            Constants.LOG.error("Failed to parse: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parse version string
     *
     * @param versionStr version string
     * @return Version Object
     */
    private static Version parseVersion(String versionStr) {
        Matcher matcher = VERSION_PATTERN.matcher(versionStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Version String: " + versionStr);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        String preRelease = matcher.group(4);
        String build = matcher.group(5);

        return new Version(major, minor, patch, preRelease, build);
    }

    /**
     * Parse version range string
     *
     * @param rangeStr version range string
     * @return VersionRange Object
     */
    private static VersionRange parseVersionRange(String rangeStr) {
        Matcher matcher = RANGE_PATTERN.matcher(rangeStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Version Range String: " + rangeStr);
        }

        String minInclusiveStr = matcher.group(1);
        String minVersionStr = matcher.group(2).trim();
        String maxVersionStr = matcher.group(3).trim();
        String maxInclusiveStr = matcher.group(4);

        boolean minInclusive = minInclusiveStr.equals("[");
        boolean maxInclusive = maxInclusiveStr.equals("]");

        Version minVersion = parseVersion(minVersionStr);
        Version maxVersion = parseVersion(maxVersionStr);

        return new VersionRange(minVersion, minInclusive, maxVersion, maxInclusive);
    }

    /**
     * Version Class
     */
    private static class Version implements Comparable<Version> {
        private final int major;
        private final int minor;
        private final int patch;
        private final String preRelease;
        private final String build;

        public Version(int major, int minor, int patch, String preRelease, String build) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.preRelease = preRelease;
            this.build = build;
        }

        @Override
        public int compareTo(Version other) {
            if (this.major != other.major) {
                return Integer.compare(this.major, other.major);
            }
            if (this.minor != other.minor) {
                return Integer.compare(this.minor, other.minor);
            }
            if (this.patch != other.patch) {
                return Integer.compare(this.patch, other.patch);
            }

            // compare pre-release
            if (this.preRelease == null && other.preRelease != null) {
                return 1;
            } else if (this.preRelease != null && other.preRelease == null) {
                return -1;
            } else if (this.preRelease != null && other.preRelease != null) {
                int preReleaseCompare = comparePreRelease(this.preRelease, other.preRelease);
                if (preReleaseCompare != 0) {
                    return preReleaseCompare;
                }
            }

            return 0;
        }

        /**
         * Compare pre-release
         *
         * @param preRelease1 pre1
         * @param preRelease2 pre2
         * @return compare result
         */
        private int comparePreRelease(String preRelease1, String preRelease2) {
            String[] parts1 = preRelease1.split("\\.");
            String[] parts2 = preRelease2.split("\\.");

            int minLength = Math.min(parts1.length, parts2.length);
            for (int i = 0; i < minLength; i++) {
                String part1 = parts1[i];
                String part2 = parts2[i];

                boolean isNumeric1 = part1.matches("\\d+");
                boolean isNumeric2 = part2.matches("\\d+");

                if (isNumeric1 && isNumeric2) {
                    int num1 = Integer.parseInt(part1);
                    int num2 = Integer.parseInt(part2);
                    if (num1 != num2) {
                        return Integer.compare(num1, num2);
                    }
                } else if (isNumeric1) {
                    return -1;
                } else if (isNumeric2) {
                    return 1;
                } else {
                    int cmp = part1.compareTo(part2);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
            }

            return Integer.compare(parts1.length, parts2.length);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(major).append(".").append(minor).append(".").append(patch);
            if (preRelease != null) {
                sb.append("-").append(preRelease);
            }
            if (build != null) {
                sb.append("+").append(build);
            }
            return sb.toString();
        }
    }

    /**
     * VersionRange Class
     */
    private static class VersionRange {
        private final Version minVersion;
        private final boolean minInclusive;
        private final Version maxVersion;
        private final boolean maxInclusive;

        public VersionRange(Version minVersion, boolean minInclusive, Version maxVersion, boolean maxInclusive) {
            this.minVersion = minVersion;
            this.minInclusive = minInclusive;
            this.maxVersion = maxVersion;
            this.maxInclusive = maxInclusive;
        }

        /**
         * Is version in range
         *
         * @param version version
         * @return in range return true，or return false
         */
        public boolean contains(Version version) {
            int minCompare = version.compareTo(minVersion);
            int maxCompare = version.compareTo(maxVersion);

            boolean minOk = minInclusive ? minCompare >= 0 : minCompare > 0;
            boolean maxOk = maxInclusive ? maxCompare <= 0 : maxCompare < 0;

            return minOk && maxOk;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(minInclusive ? "[" : "(");
            sb.append(minVersion).append(",").append(maxVersion);
            sb.append(maxInclusive ? "]" : ")");
            return sb.toString();
        }
    }

    // Debug method
    public static void test() {
        String versionStr = "1.8.9-dev";
        String rangeStr = "[1.2.3,1.25.7)";

        boolean result = isVersionInRange(versionStr, rangeStr);
        Constants.LOG.debug("{}", result);
    }
}