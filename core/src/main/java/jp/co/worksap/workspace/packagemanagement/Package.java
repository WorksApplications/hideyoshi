package jp.co.worksap.workspace.packagemanagement;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.google.common.base.Optional;

@Data
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public final class Package {
    @Nullable
    private String name;

    /**
     * @see https://github.com/chocolatey/chocolatey/wiki/CommandsInstall#version-optional
     */
    @Nullable
    private String version;

    @Nonnull
    public static Package of(@Nonnull String name) {
        return new Package(name, null);
    }

    @Nonnull
    public static Package of(@Nonnull String name, @Nullable String version) {
        return new Package(name, version);
    }

    public String getName() {
        checkState(name != null);
        return name;
    }

    public Optional<String> getVersion() {
        return Optional.fromNullable(version);
    }
}
