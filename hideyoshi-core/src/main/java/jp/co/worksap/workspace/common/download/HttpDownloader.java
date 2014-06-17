package jp.co.worksap.workspace.common.download;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Splitter;

@ParametersAreNonnullByDefault
public class HttpDownloader extends AbsHttpDownloader {
    public HttpDownloader(AuthenticationInfoProvider infoProvider) {
        super(infoProvider);
    }

    @Override
    CloseableHttpClient buildHttpClientFor(URI from) {
        return HttpClients.createDefault();
    }

    @Override
    HttpClientContext buildClientContextFor(HttpHost targetHost) {
        return HttpClientContext.create();
    }

    @Override
    void retry(URI from, File to, Header authenticateHeader) throws IOException {
        checkNotNull(authenticateHeader);
        String userName = getInfoProvider().loadUserName();
        String password = getInfoProvider().loadPassword();

        String authenticateHeaderValue = authenticateHeader.getValue();
        if (authenticateHeaderValue.startsWith("Basic ")) {
            new HttpBasicAuthDownloader(getInfoProvider(), userName, password).download(from, to);
        } else if (authenticateHeaderValue.startsWith("Digest ")) {
            String realm = null, nonce = null;

            for (String parameter : Splitter.on(',').trimResults().split(authenticateHeaderValue.substring("Digest ".length()))) {
                if (parameter.startsWith("nonce=")) {
                    String wrappedValue = parameter.substring("nonce=".length());
                    nonce = wrappedValue.substring(1, wrappedValue.length() - 1); // remove double-quote
                } else if (parameter.startsWith("realm=")) {
                    String wrappedValue = parameter.substring("realm=".length());
                    realm = wrappedValue.substring(1, wrappedValue.length() - 1); // remove double-quote
                }
            }
            new HttpDigestAuthDownloader(getInfoProvider(), userName, password, realm, nonce).download(from, to);
        } else {
            throw new IllegalArgumentException("Unknown authentication logic: " + authenticateHeaderValue);
        }
    }
}
