package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.OperatingSystem;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Files;
import com.google.common.io.Resources;

@Slf4j
public class EclipseInstaller {
    public File install(EclipseConfiguration configuration, File location) {
        try {
            String downloadUrl = findDownloadUrl(configuration);
            File downloadedFile = File.createTempFile("eclipse", ".download");
            Resources.copy(URI.create(downloadUrl).toURL(), Files.asByteSink(downloadedFile).openStream());
            File eclipseDir = new File(location, "eclipse");
            if (eclipseDir.exists()) {
                log.info("Eclipse folder already exists at {} so skip installation", eclipseDir.getAbsolutePath());
            } else {
                unpack(downloadedFile, location);
                log.info("Eclipse has been unzipped at {}", eclipseDir.getAbsolutePath());
            }
            return new File(location, "eclipse");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void unpack(File downloadedFile, File targetDir) {
        try {
            ZipFile zipped = new ZipFile(downloadedFile);
            zipped.extractAll(targetDir.getAbsolutePath());
        } catch (ZipException e) {
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
