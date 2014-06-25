package jp.co.worksap.workspace.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import jp.co.worksap.workspace.common.download.AuthenticationInfoProvider;
import jp.co.worksap.workspace.common.download.Downloader;

import org.junit.Test;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class DownloadFileTest {
    private final AuthenticationInfoProvider infoProvider = new NeverCalledProvider();

    @Test
    public void downloadFileLocal() throws IOException {
        File downloadedFile = File.createTempFile("eclipse", ".download");
        URL url = new UrlCreator().createFrom("./src/test/resources/.gitignore");
        Downloader downloadFile = Downloader.createFor(url, infoProvider);
        downloadFile.download(url, downloadedFile);
        assertTrue(Files.equal(new File("src/test/resources", ".gitignore"), downloadedFile));
    }

    @Test
    public void downloadFileHTTP() throws IOException {
        File downloadedFile = File.createTempFile("logo", ".download");
        URL url = new URL("http://career.worksap.co.jp/common/img/logo.gif");
        Downloader downloadFile = Downloader.createFor(url, infoProvider);
        downloadFile.download(url, downloadedFile);

        assertThat(downloadedFile.exists(), is(true));
        assertThat(Files.hash(downloadedFile, Hashing.md5()).asLong(), is(-3691889764638925569L));
    }
}
