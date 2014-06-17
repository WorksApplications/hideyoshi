package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.common.base.Objects;

@AllArgsConstructor
abstract class AbsHttpDownloader extends Downloader {
    @Nonnull
    @Getter(AccessLevel.PROTECTED)
    private final AuthenticationInfoProvider infoProvider;

    @Override
    public final boolean accept(String scheme) {
        return Objects.equal(scheme, "http") || Objects.equal(scheme, "https");
    }

    @Override
    public final void download(URI from, File to) throws IOException {
        HttpHost targetHost = new HttpHost(from.getHost(), from.getPort(), from.getScheme());
        HttpClientContext localContext = buildClientContextFor(targetHost);
        HttpGet httpGet = new HttpGet(from);

        @Cleanup
        CloseableHttpClient client = buildHttpClientFor(from);
        @Cleanup
        CloseableHttpResponse response = client.execute(targetHost, httpGet, localContext);
        int status = response.getStatusLine().getStatusCode();

        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            final long fileSize = entity.getContentLength();
            final String fileName = from.toURL().getFile();
            @Cleanup
            InputStream inputStream = entity.getContent();

            copyToLocal(to, fileName, inputStream, fileSize);
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            client.close();
            retry(from, to, response.getFirstHeader("WWW-Authenticate"));
        } else {
            throw new IllegalStateException(
                    String.format("Unexpected status code (%d) from %s", status, from)
            );
        }
    }

    abstract void retry(URI from, File to, Header firstHeader) throws IOException;
    abstract CloseableHttpClient buildHttpClientFor(URI from);
    abstract HttpClientContext buildClientContextFor(HttpHost targetHost);
}
