package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.UnArchiver;
import lombok.extern.slf4j.Slf4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;
import com.google.common.io.Resources;

@Slf4j
public class EclipseInstaller {
    public File install(EclipseConfiguration configuration, File location) {
        try {
            String downloadUrl = findDownloadUrl(configuration);
            File downloadedFile = File.createTempFile("eclipse", "." + Files.getFileExtension(downloadUrl));
            Resources.copy(URI.create(downloadUrl).toURL(), Files.asByteSink(downloadedFile).openStream());
            File eclipseDir = new File(location, "eclipse");
            if (eclipseDir.exists()) {
                log.info("Eclipse folder already exists at {} so skip installation", eclipseDir.getAbsolutePath());
            } else {
                new UnArchiver().extract(downloadedFile, location);
                log.info("Eclipse has been unzipped at {}", eclipseDir.getAbsolutePath());
            }
            return eclipseDir;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nonnull
    @VisibleForTesting
    String findDownloadUrl(EclipseConfiguration configuration) throws IOException {
        Version version = configuration.getVersion();
        String downloadFrom = configuration.getDownloadFrom().get(OperatingSystem.create());
        if (downloadFrom == null) {
            return new DownloadUrlFinder().findDownloadUrl(version);
        } else {
            if (downloadFrom.startsWith("./")) {
                // add file:// to convert relative path to URL
                String simplePath = Files.simplifyPath(new File(".").getAbsolutePath().replace('\\', '/'));
                return new File(simplePath, downloadFrom.substring(2)).toURI().toURL().toString();
            } else {
                throw new IllegalArgumentException("not supported downloadFrom: " + downloadFrom);
            }
        }
    }
}
