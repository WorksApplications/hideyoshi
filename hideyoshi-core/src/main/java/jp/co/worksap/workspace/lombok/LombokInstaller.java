package jp.co.worksap.workspace.lombok;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import jp.co.worksap.workspace.common.PipingDaemon;
import jp.co.worksap.workspace.common.UrlCreator;
import jp.co.worksap.workspace.common.download.AuthenticationInfoProvider;
import jp.co.worksap.workspace.common.download.Downloader;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;

@Slf4j
public class LombokInstaller {
    private static final String COPIED_FILE_NAME = "lombok.jar";

    public void install(Optional<LombokConfiguration> lombok, File location, AuthenticationInfoProvider infoProvider) {
        if (!lombok.isPresent()) {
            return;
        }
        checkArgument(location.isDirectory(), "2nd argument should be directory to install Lombok");

        File downloadedFile;
        try {
            downloadedFile = download(lombok.get(), location, infoProvider);
            executeInstaller(location, downloadedFile);
        } catch (IOException e) {
            throw new IllegalStateException("fail to install Lombok", e);
        }
    }

    private void executeInstaller(File location, File downloadedFile) throws IOException {
        String javaPath = Joiner.on(File.separator).join(System.getProperty("java.home"), "bin", "java");
        ProcessBuilder builder = new ProcessBuilder(javaPath, "-jar", COPIED_FILE_NAME, "install", ".");
        builder.directory(location);
        log.info("execute command ({}) to install lombok at {}", builder.command(), location.getAbsolutePath());
        Process process = builder.start();
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            int statusCode = process.waitFor();
            if (statusCode != 0) {
                throw new IllegalArgumentException("Failed to install lombok, status code is " + statusCode);
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            process.destroy();
        }
    }

    private File download(LombokConfiguration lombok, File location, AuthenticationInfoProvider infoProvider)
            throws IOException {
        if (lombok.getDownloadFrom().isPresent()) {
            URI lombokUri = lombok.getDownloadFrom().get();
            Downloader downloader = Downloader.createFor(lombokUri, infoProvider);
            File copied = new File(location, COPIED_FILE_NAME);
            downloader.download(lombokUri, copied);
            return copied;
        }

        URI downloadUri = lombok.getUrlToDownload();
        File localCopy = new File(location, COPIED_FILE_NAME);
        Resources.copy(new UrlCreator().createFrom(downloadUri), Files.asByteSink(localCopy).openStream());
        return localCopy;
    }

    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Lombok", "stdout");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Lombok", "stderr");
        daemon.start();
    }
}
