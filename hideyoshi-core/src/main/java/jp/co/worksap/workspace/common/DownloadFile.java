package jp.co.worksap.workspace.common;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jp.co.worksap.workspace.common.download.Downloader;
import jp.co.worksap.workspace.common.download.StandardDownloader;

/**
 * @deprecated use {@link Downloader} instead.
 */
public class DownloadFile {
    public File download(URL url, File outputFile) throws IOException {
        Downloader downloader = createFor(url);
        try {
            downloader.download(url.toURI(), outputFile);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        return outputFile;
    }

    private Downloader createFor(URL url) {
        return new StandardDownloader();
    }

}
