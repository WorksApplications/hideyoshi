package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

public abstract class Downloader {
    @CheckReturnValue
    public abstract boolean accept(@Nullable String scheme);

    public abstract void download(URI from, File to) throws IOException;

    public static Downloader createFor(URI uri) {
        return new StandardDownloader();
    }

    public static Downloader createFor(URL url) {
        try {
            return createFor(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void download(URL from, File to) throws IOException {
        try {
            download(from.toURI(), to);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
