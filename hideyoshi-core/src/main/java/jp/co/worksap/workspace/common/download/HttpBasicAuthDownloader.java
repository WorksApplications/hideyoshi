package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.net.URI;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@ParametersAreNonnullByDefault
final class HttpBasicAuthDownloader extends AbsHttpDownloader {
    private final String userName;
    private final String password;

    HttpBasicAuthDownloader(AuthenticationInfoProvider infoProvider, String userName, String password) {
        super(infoProvider);
        this.userName = userName;
        this.password = password;
    }

    @Override
    public CloseableHttpClient buildHttpClientFor(URI from) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(from.getHost(), from.getPort()),
                new UsernamePasswordCredentials(userName, password));

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        return client;
    }

    @Override
    public HttpClientContext buildClientContextFor(HttpHost targetHost) {
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        return localContext;
    }

    @Override
    void retry(URI from, File to, Header firstHeader) {
        throw new IllegalArgumentException("Username or password is incorrect.");
    }

}
