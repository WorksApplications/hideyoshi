package jp.co.worksap.workspace.common;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(using = OperatingSystemDeserializer.class)
public enum OperatingSystem {
    WIN32("win", 32), WIN64("win", 64), OSX32("osx", 32), OSX64("osx", 64), LINUX32("linux", 32), LINUX64("linux", 64);

    private static final String WINDOWS = "win";

    public String getName() {
        return name;
    }

    public int getBits() {
        return bits;
    }

    @Nonnull
    private final String name;
    @Nonnegative
    private final int bits;

    OperatingSystem(@Nonnull String name, @Nonnegative int bits) {
        checkArgument(bits == 32 || bits == 64);
        this.name = checkNotNull(name);
        this.bits = bits;
    }

    @Nonnull
    public String appendExtensionTo(@Nonnull String fileName) {
        checkNotNull(fileName);
        if (name.equals(WINDOWS)) {
            return fileName + ".exe";
        } else {
            return fileName;
        }
    }

    public static OperatingSystem create() {
        if (isWindows()) {
            if (is32bit()) {
                return WIN32;
            } else {
                return WIN64;
            }
        } else if (isOsx()) {
            if (is32bit()) {
                return OSX32;
            } else {
                return OSX64;
            }
        } else {
            if (is32bit()) {
                return LINUX32;
            } else {
                return LINUX64;
            }
        }
    }

    public static OperatingSystem fromString(@Nonnull String string) {
        return new OperatingSystemLiteralConverter().apply(string);
    }

    private static boolean is32bit() {
        String osName = System.getProperty("os.arch");
        return osName.contains("32");
    }

    /**
     * @see https://developer.apple.com/library/mac/technotes/tn2002/tn2110.html
     */
    private static boolean isOsx() {
        String osName = System.getProperty("os.name");
        return osName.contains("OS X");
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName.startsWith("Windows");
    }
}
