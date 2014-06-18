package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.UnArchiver;
import jp.co.worksap.workspace.common.download.AuthenticationInfoProvider;
import jp.co.worksap.workspace.common.download.Downloader;
import lombok.extern.slf4j.Slf4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;

@Slf4j
public class EclipseInstaller {
    public File install(EclipseConfiguration configuration, File location, AuthenticationInfoProvider infoProvider) {
        try {
            String downloadUrl = findDownloadUrl(configuration);
            File eclipseDir = new File(location, "eclipse");
            if (eclipseDir.exists()) {
                log.info("Eclipse folder already exists at {} so skip installation", eclipseDir.getAbsolutePath());
            } else {
                final File downloadedFile;
                URI downloadUri = URI.create(downloadUrl);
                if ("file".equals(downloadUri.getScheme())) {
                    // No need to copy: just extract existing file
                    downloadedFile = new File(downloadUri.getPath());
                } else {
                    downloadedFile = File.createTempFile("eclipse", "." + Files.getFileExtension(downloadUrl));
                    log.info("downloading Eclipse from {}...", downloadUrl);
                    Downloader downloader = Downloader.createFor(downloadUri, infoProvider);
                    downloader.download(downloadUri, downloadedFile);
                    log.info("Eclipse has been downloaded.");
                }
                log.info("extracting Eclipse...");
                new UnArchiver().extract(downloadedFile, location);
                log.info("Eclipse has been extracted at {}", eclipseDir.getAbsolutePath());
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
