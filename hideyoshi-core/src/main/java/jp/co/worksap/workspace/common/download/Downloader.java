package jp.co.worksap.workspace.common.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Downloader {
    private static final int BUFFER_SIZE = 1024;

    @CheckReturnValue
    public abstract boolean accept(@Nullable String scheme);

    public abstract void download(URI from, File to) throws IOException;

    public static Downloader createFor(URI uri) {
        HttpDownloader http = new HttpDownloader();
        if (http.accept(uri.getScheme())) {
            return http;
        }

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

    final protected void copyToLocal(File to, String fileName, @WillNotClose InputStream in,
            final long fileSize) throws FileNotFoundException, IOException {
        @Cleanup BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));
        BufferedInputStream buffer = new BufferedInputStream(in);
        byte data[] = new byte[BUFFER_SIZE];
        int count, percentNew = 0, percentOld = 0;
        long totalDataRead = 0;
        while ((count = buffer.read(data, 0, BUFFER_SIZE)) >= 0) {
            totalDataRead += count;
            out.write(data, 0, count);
            percentNew = (int) ((totalDataRead * 100) / fileSize);
            if (percentNew >= percentOld + 5) {
                log.info("{} {}% Done", fileName, percentNew);
                percentOld = percentNew;
            }
        }
        out.flush();
        log.info("{} Download Complete!", fileName);
    }
}
