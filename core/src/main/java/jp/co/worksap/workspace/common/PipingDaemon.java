package jp.co.worksap.workspace.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PipingDaemon implements Runnable {
    private final InputStream inputStream;
    private final String targetType;
    private final String type;

    public static Thread createThread(@Nonnull InputStream inputStream, @Nonnull String targetType, @Nonnull String pipeType) {
        Thread daemon = new Thread(new PipingDaemon(inputStream, targetType, pipeType));
        daemon.setDaemon(false); // Log is important, we should wait this thread finishes its work
        daemon.setName(String.format("%s %s logging daemon", targetType, pipeType));
        return daemon;
    }

    PipingDaemon(@Nonnull InputStream inputStream, @Nonnull String targetType, @Nonnull String pipeType) {
        this.inputStream = checkNotNull(inputStream);
        this.targetType = checkNotNull(targetType);
        this.type = checkNotNull(pipeType);
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("{} {}: {}", targetType, type, line);
            }
        } catch (IOException e) {
            log.error("error occurs while logging " + type, e);
            throw new IllegalStateException(e);
        }
        log.trace("{} {} logging daemon has been stopped.", targetType, type);
    }
}