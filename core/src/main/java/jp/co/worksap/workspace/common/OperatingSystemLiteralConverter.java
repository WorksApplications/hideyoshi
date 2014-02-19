package jp.co.worksap.workspace.common;

import javax.annotation.Nullable;

import com.google.common.base.Function;

public class OperatingSystemLiteralConverter implements
        Function<String, OperatingSystem> {

    @Override
    @Nullable
    public OperatingSystem apply(@Nullable String input) {
        switch (input.toLowerCase()) {
        case "win.32":
            return OperatingSystem.WIN32;
        case "win.64":
            return OperatingSystem.WIN64;
        case "osx.32":
            return OperatingSystem.OSX32;
        case "osx.64":
            return OperatingSystem.OSX64;
        case "linux.32":
            return OperatingSystem.LINUX32;
        case "linux.64":
            return OperatingSystem.LINUX64;
        default:
            throw new IllegalArgumentException(
                    "OperatingSystem should be formatted as name.bits (like \"win.32\")");
        }
    }

}
