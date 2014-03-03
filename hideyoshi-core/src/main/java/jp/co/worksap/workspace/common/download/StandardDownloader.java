package jp.co.worksap.workspace.common.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardDownloader extends Downloader {

    @Override
    public boolean accept(String scheme) {
        return true;
    }

    @Override
    public void download(URI from, File to) throws IOException {
        URL url = from.toURL();
        String fileName = url.getFile();
        URLConnection connection = url.openConnection();

        @Cleanup BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        @Cleanup BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(to));

        final long fileSize = connection.getContentLength();
        byte data[] = new byte[1024];
        int count, percentNew = 0, percentOld = 0;
        long totalDataRead = 0;
        while ((count = in.read(data, 0, 1024)) >= 0) {
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
