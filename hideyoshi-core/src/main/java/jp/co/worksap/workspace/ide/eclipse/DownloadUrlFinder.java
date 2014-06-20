package jp.co.worksap.workspace.ide.eclipse;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Strings;

import jp.co.worksap.workspace.common.OperatingSystem;

@ParametersAreNonnullByDefault
public class DownloadUrlFinder {

    @Nonnull
    String findDownloadUrl(Version version) throws IOException {
        OperatingSystem system = OperatingSystem.create();
        return findDownloadUrl(version, system);
    }

    @Nonnull
    String findDownloadUrl(Version version, OperatingSystem system) throws IOException {
        checkNotNull(version);

        try (InputStream urlInfo = version.loadUrlInformation()) {
            if (urlInfo == null) {
                throw new IllegalArgumentException("specified version (" + version + ") is not supported yet.");
            }

            Properties urlInfoProperty = new Properties();
            urlInfoProperty.load(urlInfo);
            String key = String.format("javaee.%s.%d", system.getName(), system.getBits());

            String downloadUrl = urlInfoProperty.getProperty(key);
            if (Strings.isNullOrEmpty(downloadUrl)) {
                throw new UnsupportedOperationException("download URL for specified version (" + version + ") is unknown");
            }

            return downloadUrl;
        }
    }

}
