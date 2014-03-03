package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import lombok.Cleanup;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Objects;

public class HttpDownloader extends Downloader {

    @Override
    public boolean accept(String scheme) {
        return Objects.equal(scheme, "http") || Objects.equal(scheme, "https");
    }

    @Override
    public void download(URI from, File to) throws IOException {
        HttpGet httpGet = new HttpGet(from);

        @Cleanup CloseableHttpClient client = HttpClients.createDefault();
        @Cleanup CloseableHttpResponse response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        final long fileSize = entity.getContentLength();
        final String fileName = from.toURL().getFile();
        @Cleanup InputStream inputStream = entity.getContent();

        copyToLocal(to, fileName, inputStream, fileSize);
    }

}
