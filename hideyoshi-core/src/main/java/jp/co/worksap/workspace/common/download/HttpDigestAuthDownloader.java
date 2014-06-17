package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

@ParametersAreNonnullByDefault
final class HttpDigestAuthDownloader extends AbsHttpDownloader {
    private final String userName;
    private final String password;
    private final String realm;
    private final String nonce;

    HttpDigestAuthDownloader(AuthenticationInfoProvider infoProvider, String userName, String password, String realm, String nonce) {
        super(infoProvider);
        this.userName = userName;
        this.password = password;
        this.realm = realm;
        this.nonce = nonce;
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
        DigestScheme digestAuth = new DigestScheme(); 
        digestAuth.overrideParamter("realm", realm);
        digestAuth.overrideParamter("nonce", nonce);
        authCache.put(targetHost, digestAuth);

        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        return localContext;
    }

    @Override
    void retry(URI from, File to, Header firstHeader) throws IOException {
        throw new IllegalArgumentException("Username or password is incorrect.");
    }

}
