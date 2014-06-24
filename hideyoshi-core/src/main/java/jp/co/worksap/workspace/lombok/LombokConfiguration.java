package jp.co.worksap.workspace.lombok;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.google.common.base.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class LombokConfiguration {
    @Nullable
    private String version;

    @Nullable
    private URL downloadFrom;

    URL getUrlToDownload() {
        try {
            return URI.create(String.format("https://projectlombok.googlecode.com/files/lombok-%s.jar", version)).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nonnull
    Optional<URL> getDownloadFrom() {
        return Optional.fromNullable(downloadFrom);
    }

    public static LombokConfiguration fromString(@Nonnull String version) {
        checkNotNull(version);
        return new LombokConfiguration(version, null);
    }
}
