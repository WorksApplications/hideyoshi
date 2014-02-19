package jp.co.worksap.workspace.cli;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

final class LogConfigurator {

    /**
     * <p>Change configuration of LogBack.
     * Note that only this class accesses to LogBack, other classes should
     * access SLF4J instead of LogBack to keep portability.</p>
     *
     * @param option
     */
    void configureLogger(CliOption option) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        if (option.isDebug()) {
            // see http://stackoverflow.com/a/3838108
            root.setLevel(Level.ALL);
        } else {
            root.setLevel(Level.INFO);
        }

        if (option.getLogFile().isPresent()) {
            File logFile = option.getLogFile().get();
            MDC.put("logfile", logFile.getAbsolutePath());
        } else {
            root.getAppender("SIFT").stop();
        }
    }

}
