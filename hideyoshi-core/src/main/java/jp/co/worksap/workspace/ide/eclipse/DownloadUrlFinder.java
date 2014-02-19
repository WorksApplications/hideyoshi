package jp.co.worksap.workspace.ide.eclipse;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.OperatingSystem;

public class DownloadUrlFinder {

    @Nonnull
    String findDownloadUrl(@Nonnull Version version) throws IOException {
        checkNotNull(version);

        try (InputStream urlInfo = version.loadUrlInformation()) {
            Properties urlInfoProperty = new Properties();
            urlInfoProperty.load(urlInfo);
            OperatingSystem system = OperatingSystem.create();
            String key = String.format("javaee.%s.%d", system.getName(), system.getBits());

            String downloadUrl = urlInfoProperty.getProperty(key);
            if (downloadUrl == null) {
                throw new IllegalArgumentException("download URL for specified version (" + version + ") is unknown");
            }

            return downloadUrl;
        }
    }

}
