package jp.co.worksap.workspace.lombok;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

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
    private URI downloadFrom;

    URI getUrlToDownload() {
        return URI.create(String.format("https://projectlombok.googlecode.com/files/lombok-%s.jar", version));
    }

    @Nonnull
    Optional<URI> getDownloadFrom() {
        return Optional.fromNullable(downloadFrom);
    }

    public static LombokConfiguration fromString(@Nonnull String version) {
        checkNotNull(version);
        return new LombokConfiguration(version, null);
    }
}
