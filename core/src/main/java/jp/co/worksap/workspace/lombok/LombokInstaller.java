package jp.co.worksap.workspace.lombok;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import jp.co.worksap.workspace.common.PipingDaemon;

import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.io.Files;
import com.google.common.io.Resources;

@Slf4j
public class LombokInstaller {
    private static final String COPIED_FILE_NAME = "lombok.jar";

    public void install(Optional<LombokConfiguration> lombok, File location) {
        if (!lombok.isPresent()) {
            return;
        }
        checkArgument(location.isDirectory(), "2nd argument should be directory to install Lombok");

        File downloadedFile;
        try {
            downloadedFile = download(lombok.get(), location);
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

    private File download(LombokConfiguration lombok, File location)
            throws IOException {
        if (lombok.getDownloadFrom().isPresent()) {
            // TODO support remote downloading: now we expect that this configuration specify relative path
            File source = new File(lombok.getDownloadFrom().get());
            File copied = new File(location, COPIED_FILE_NAME);
            Files.copy(source, copied);
            return copied;
        }

        URL downloadUrl = lombok.getUrlToDownload();
        File localCopy = new File(location, COPIED_FILE_NAME);
        Resources.copy(downloadUrl, Files.newOutputStreamSupplier(localCopy).getOutput());
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