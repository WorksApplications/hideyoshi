package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import lombok.Cleanup;

public class StandardDownloader extends Downloader {

    @Override
    public boolean accept(String scheme) {
        return true;
    }

    @Override
    public void download(URI from, File to) throws IOException {
        URL url = convertToUrl(from);
        String fileName = url.getFile();
        URLConnection connection = url.openConnection();

        @Cleanup InputStream in = connection.getInputStream();
        final long fileSize = connection.getContentLength();

        copyToLocal(to, fileName, in, fileSize);
    }

    private URL convertToUrl(URI uri) throws MalformedURLException {
        if (uri.isAbsolute()) {
            return uri.toURL();
        } else {
            return new File(".", uri.toString()).toURI().toURL();
        }
    }
}
