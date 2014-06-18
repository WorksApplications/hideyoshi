package jp.co.worksap.workspace.cli;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

public class SystemInAuthenticationInfoProviderTest {
    @Rule
    public TextFromStandardInputStream mockedSystemIn = TextFromStandardInputStream.emptyStandardInputStream();

    @Rule
    public StandardOutputStreamLog mockedSystemOut = new StandardOutputStreamLog();

    @Test
    public void test() throws IOException {
        mockedSystemIn.provideText("username\npassword\n");
        SystemInAuthenticationInfoProvider infoProvider = new SystemInAuthenticationInfoProvider();

        assertThat(infoProvider.loadUserName(), is("username"));
        assertThat(mockedSystemOut.getLog(),
                anyOf(is("User name:\r\n"), is("User name:\n")));

        mockedSystemOut.clear();

        assertThat(infoProvider.loadPassword(), is("password"));
        assertThat(mockedSystemOut.getLog(),
                anyOf(is("Password:\r\n"), is("Password:\n")));
    }

}
