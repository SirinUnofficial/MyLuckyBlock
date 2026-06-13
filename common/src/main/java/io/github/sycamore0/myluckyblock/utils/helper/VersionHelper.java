package io.github.sycamore0.myluckyblock.utils.helper;

import io.github.sycamore0.myluckyblock.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionHelper {
    /* Regex for strict semantic versions: major.minor.patch[-pre][+build] */
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "^(\\d+)\\.(\\d+)\\.(\\d+)(?:-([a-zA-Z0-9.-]+))?(?:\\+([a-zA-Z0-9.-]+))?$");
    /* Regex for version ranges: [1.0,2.0) or (1.0,2.0] etc. */
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
        if ("*".equals(rangeStr)) {
            return true;
        }
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
     * <p>
     * If the input contains a space, only the part before the first space is considered.
     *
     * @param versionStr version string
     * @return Version Object
     */
    private static Version parseVersion(String versionStr) {
        /* Strip everything after the first space (if any) */
        int spacePos = versionStr.indexOf(' ');
        if (spacePos != -1) {
            versionStr = versionStr.substring(0, spacePos);
        }

        /* Extract pre-release and build metadata before auto-completion */
        String preRelease = null;
        String build = null;

        // Extract build metadata (+build)
        int plusPos = versionStr.indexOf('+');
        if (plusPos != -1) {
            build = versionStr.substring(plusPos + 1);
            versionStr = versionStr.substring(0, plusPos);
        }

        // Extract pre-release version (-pre)
        int dashPos = versionStr.indexOf('-');
        if (dashPos != -1) {
            preRelease = versionStr.substring(dashPos + 1);
            versionStr = versionStr.substring(0, dashPos);
        }

        /* Auto-complete missing parts for the numeric version */
        if (versionStr.matches("^\\d+$")) {
            versionStr += ".0.0";
        } else if (versionStr.matches("^\\d+\\.\\d+$")) {
            versionStr += ".0";
        }

        /* Reconstruct full version string */
        StringBuilder fullVersion = new StringBuilder(versionStr);
        if (preRelease != null) {
            fullVersion.append('-').append(preRelease);
        }
        if (build != null) {
            fullVersion.append('+').append(build);
        }

        Matcher matcher = VERSION_PATTERN.matcher(fullVersion.toString());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid Version String: " + versionStr);
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = Integer.parseInt(matcher.group(3));
        String parsedPreRelease = matcher.group(4);
        String parsedBuild = matcher.group(5);

        return new Version(major, minor, patch, parsedPreRelease, parsedBuild);
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

    private record Version(int major, int minor, int patch, String preRelease, String build) implements Comparable<Version> {
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
            } else if (this.preRelease != null) {
                // && other.preRelease != null
                int preReleaseCompare = comparePreRelease(this.preRelease, other.preRelease);
                if (preReleaseCompare != 0) {
                    return preReleaseCompare;
                }
            }

            // compare build data "+build.1"
            if (this.build == null && other.build != null) {
                return -1;
            } else if (this.build != null && other.build == null) {
                return 1;
            } else if (this.build != null) {
                // && other.build != null
                return this.build.compareTo(other.build);
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

    private record VersionRange(Version minVersion, boolean minInclusive, Version maxVersion, boolean maxInclusive) {
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
}