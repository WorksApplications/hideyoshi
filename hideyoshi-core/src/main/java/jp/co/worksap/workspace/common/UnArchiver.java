package jp.co.worksap.workspace.common;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

public class UnArchiver {
    public void extract(File source, File destDirectory) {
        final AbstractUnArchiver unarchiver;
        if (source.getName().endsWith(".gz")) {
            unarchiver = new TarGZipUnArchiver();
        } else if (source.getName().endsWith(".zip")) {
            unarchiver = new ZipUnArchiver();
        } else {
            throw new IllegalArgumentException("Unknown format: " + source.getName());
        }

        destDirectory.mkdirs();
        unarchiver.setSourceFile(source);
        unarchiver.setDestDirectory(destDirectory);
        unarchiver.enableLogging(new SLf4jBridgingLogger( org.codehaus.plexus.logging.Logger.LEVEL_INFO, "console" ));
        unarchiver.extract();
    }

    @Slf4j
    private static final class SLf4jBridgingLogger extends AbstractLogger {

        public SLf4jBridgingLogger(int threshold, String name) {
            super(threshold, name);
        }

        @Override
        public void debug(String message, Throwable throwable) {
            log.debug(message, throwable);
        }

        @Override
        public void info(String message, Throwable throwable) {
            log.info(message, throwable);
        }

        @Override
        public void warn(String message, Throwable throwable) {
            log.warn(message, throwable);
        }

        @Override
        public void error(String message, Throwable throwable) {
            log.error(message, throwable);
        }

        @Override
        public void fatalError(String message, Throwable throwable) {
            log.error(message, throwable);
        }

        @Override
        public Logger getChildLogger(String name) {
            return this;
        }
    }
}
