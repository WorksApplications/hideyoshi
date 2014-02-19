package jp.co.worksap.workspace.ide.eclipse;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

final class VersionLiteralConverter implements Function<String, Version> {
    private static final ImmutableMap<String, String> CODENAMES;

    static {
        Properties codenames = new Properties();
        try {
            try (InputStream input = Version.class.getResourceAsStream("codenames.properties");) {
                codenames.load(input);
            }
            CODENAMES = Maps.fromProperties(codenames);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Nullable
    public Version apply(@Nullable String input) {
        checkNotNull(input);
        input = input.toLowerCase();
        if (CODENAMES.containsKey(input)) {
            // convert code name to number
            input = CODENAMES.get(input);
        }

        String[] split = input.split("\\.", 3);
        int major = Integer.parseInt(split[0]);
        int minor = 0;
        int patch = 0;
        if (split.length > 1) {
            minor = Integer.parseInt(split[1]);
            if (split.length > 2) {
                patch = Integer.parseInt(split[2]);
            }
        }

        return new Version(major, minor, patch);
    }
}