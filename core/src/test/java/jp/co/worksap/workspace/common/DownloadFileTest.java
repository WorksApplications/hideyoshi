package jp.co.worksap.workspace.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class DownloadFileTest {
    private String relativeToAbsolutePath(String url) throws IOException {
        if (url.startsWith("./")) {
            String simplePath = Files.simplifyPath(new File(".").getAbsolutePath().replace('\\', '/'));
            return new File(simplePath, url.substring(2)).toURI().toURL().toString();
        } else {
            return url;
        }
    }

    @Test
    public void downloadFileLocal() throws IOException {
        File downloadedFile = File.createTempFile("eclipse", ".download");
        DownloadFile downloadFile = new DownloadFile();
        URL url = new URL(relativeToAbsolutePath("./src/test/resources/.gitignore"));
        downloadFile.download(url, downloadedFile);
        assertTrue(Files.equal(new File("src/test/resources", ".gitignore"), downloadedFile));
    }

    @Test
    public void downloadFileHTTP() throws IOException {
        File downloadedFile = File.createTempFile("logo", ".download");
        DownloadFile downloadFile = new DownloadFile();
        URL url = new URL("http://career.worksap.co.jp/common/img/logo.gif");
        downloadFile.download(url, downloadedFile);

        assertThat(downloadedFile.exists(), is(true));
        assertThat(Files.hash(downloadedFile, Hashing.md5()).asLong(), is(-3691889764638925569L));
    }
}
