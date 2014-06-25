package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;

import jp.co.worksap.workspace.common.OperatingSystem;

import org.junit.Test;

public class DownloadUrlFinderTest {

    @Test
    public void test() throws IOException {
        DownloadUrlFinder finder = new DownloadUrlFinder();

        assertThat(
                finder.findDownloadUrl(Version.fromString("4.3.2"), OperatingSystem.WIN32),
                is("http://ftp.jaist.ac.jp/pub/eclipse/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-win32.zip"));

        assertThat(
                finder.findDownloadUrl(Version.fromString("4.2.2"), OperatingSystem.WIN64),
                is("http://ftp.jaist.ac.jp/pub/eclipse/technology/epp/downloads/release/juno/SR2/eclipse-jee-juno-SR2-win32-x86_64.zip"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidVersion() throws IOException {
        DownloadUrlFinder finder = new DownloadUrlFinder();

        // 1.0.0 is well-formed, but it doesn't exist
        finder.findDownloadUrl(Version.fromString("1.0.0"), OperatingSystem.WIN32);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNotSupportedVersion() throws IOException {
        DownloadUrlFinder finder = new DownloadUrlFinder();

        finder.findDownloadUrl(Version.fromString("4.2.2"), OperatingSystem.OSX32);
    }
}
