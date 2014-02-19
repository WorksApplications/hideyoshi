package jp.co.worksap.workspace.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogConfiguratorTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public final TestName testName = new TestName();

    @Test
    public void defaultLogLevelShouldBeInfo() {
        CliOption option = new CliOption();
        option.setDebug(false);
        new LogConfigurator().configureLogger(option);

        Logger logger = LoggerFactory.getLogger(getClass());
        assertThat(logger.isDebugEnabled(), is(false));
        assertThat(logger.isInfoEnabled(), is(true));
    }

    @Test
    public void logLevelShouldBeDebugInDebugMode() {
        CliOption option = new CliOption();
        option.setDebug(true);
        new LogConfigurator().configureLogger(option);

        Logger logger = LoggerFactory.getLogger(getClass());
        assertThat(logger.isDebugEnabled(), is(true));
        assertThat(logger.isInfoEnabled(), is(true));
    }

    @Test
    public void fileAppenderIsActive() throws IOException {
        File logFile = tempFolder.newFile();
        CliOption option = new CliOption();
        option.setLogFile(logFile);
        new LogConfigurator().configureLogger(option);

        logFile.delete();
        executeLog();
        assertThat(logFile.exists(), is(true));
    }

    @Test
    public void stdoutAppenderIsActive() {
        CliOption option = new CliOption();
        new LogConfigurator().configureLogger(option);

        assertThatStdoutAppenderIsActive();
    }

    @Test
    public void stdoutAppenderIsActiveEvenIfLogFileIsSpecified() throws IOException {
        CliOption option = new CliOption();
        option.setLogFile(tempFolder.newFile());
        new LogConfigurator().configureLogger(option);

        assertThatStdoutAppenderIsActive();
    }

    private void assertThatStdoutAppenderIsActive() {
        PrintStream defaultStdout = System.out;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(buffer);
            System.setOut(stream);

            assertThat(buffer.size(), is(0));
            executeLog();
            assertThat(buffer.size(), is(not(0)));
        } finally {
            System.setOut(defaultStdout);
        }
    }

    private void executeLog() {
        LoggerFactory.getLogger(getClass()).info("hello, {}", testName.getMethodName());
    }
}
