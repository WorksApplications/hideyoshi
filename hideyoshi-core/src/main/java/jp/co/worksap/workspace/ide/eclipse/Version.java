package jp.co.worksap.workspace.ide.eclipse;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import lombok.Value;

import com.google.common.collect.ComparisonChain;

/**
 * @see http://semver.org/
 * @author Kengo TODA (toda_k@worksap.co.jp)
 */
@JsonDeserialize(using = VersionDeserializer.class)
@Value
public class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;

    @Override
    public int compareTo(Version another) {
        checkNotNull(another);

        return ComparisonChain.start()
                .compare(major, another.major)
                .compare(minor, another.minor)
                .compare(patch, another.patch)
                .result();
    }

    @Nonnull
    public static Version fromString(@Nonnull String string) {
        return new VersionLiteralConverter().apply(string);
    }

    @CheckForNull
    InputStream loadUrlInformation() {
        return getClass().getResourceAsStream(String.format("downloads-%d.%d.%d.properties", major, minor, patch));
    }
}
