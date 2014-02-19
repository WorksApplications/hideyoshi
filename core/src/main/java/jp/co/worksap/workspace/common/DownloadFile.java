package jp.co.worksap.workspace.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.google.common.io.Closer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadFile {
    public File download(URL url, File outputFile) throws IOException {
        Closer closer = Closer.create();
        try {

            URLConnection connection = url.openConnection();

            long fileSize = connection.getContentLength();
            long totalDataRead = 0;
            BufferedInputStream in = closer.register(new BufferedInputStream(connection.getInputStream()));
            BufferedOutputStream out = closer.register(new BufferedOutputStream(new FileOutputStream(outputFile)));
            byte data[] = new byte[1024];
            int count, percentNew = 0, percentOld = 0;
            while ((count = in.read(data, 0, 1024)) >= 0) {
                totalDataRead += count;
                out.write(data, 0, count);
                percentNew = (int) ((totalDataRead * 100) / fileSize);
                if (percentNew >= percentOld + 5) {
                    log.info(url.getFile() + " " + percentNew + "% Done");
                    percentOld = percentNew;
                }
            }
            out.flush();
            log.info(url.getFile() + " Download Complete!");
        } finally {
            closer.close();
        }
        return outputFile;
    }

}
