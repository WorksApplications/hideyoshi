package jp.co.worksap.workspace.common.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.Cleanup;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;

@ParametersAreNonnullByDefault
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
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            final long fileSize = entity.getContentLength();
            final String fileName = from.toURL().getFile();
            @Cleanup InputStream inputStream = entity.getContent();

            copyToLocal(to, fileName, inputStream, fileSize);
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            System.out.printf("User name:");
            String userName = scanner.next();
            System.out.printf("%nPassword:");
            String password = scanner.next();
            Header authenticateHeader = response.getFirstHeader("WWW-Authenticate");
            String authenticateValue = authenticateHeader.getValue();

            if (authenticateValue.startsWith("Basic ")) {
                downloadViaBasicAuth(from, to, userName, password);
            } else if (authenticateValue.startsWith("Digest ")) {
                String realm = null, nonce = null;

                for (String parameter : Splitter.on(',').trimResults().split(authenticateValue.substring("Digest ".length()))) {
                    if (parameter.startsWith("nonce=")) {
                        String wrappedValue = parameter.substring("nonce=".length());
                        nonce = wrappedValue.substring(1, wrappedValue.length() - 1); // remove double-quote
                    } else if (parameter.startsWith("realm=")) {
                        String wrappedValue = parameter.substring("realm=".length());
                        realm = wrappedValue.substring(1, wrappedValue.length() - 1); // remove double-quote
                    }
                }
                downloadViaDigestAuth(from, to, userName, password, realm, nonce);
            } else {
                throw new UnsupportedOperationException("Unknown authentication logic: " + authenticateValue);
            }
        } else {
            throw new IllegalStateException(
                    String.format("Unexpected status code (%d) from %s", status, from)
            );
        }
    }

    /**
     * <p>Use basic authentication to download file.
     * @see http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientPreemptiveBasicAuthentication.java
     */
    private void downloadViaBasicAuth(URI from, File to, String userName, String password) throws IOException {
        HttpHost targetHost = new HttpHost(from.getHost(), from.getPort(), from.getScheme());
        HttpClientContext localContext = buildClientContextFor(targetHost);
        HttpGet httpGet = new HttpGet(from);

        @Cleanup
        CloseableHttpClient client = buildHttpClientFor(from, userName, password);
        @Cleanup
        CloseableHttpResponse response = client.execute(targetHost, httpGet, localContext);
        int status = response.getStatusLine().getStatusCode();

        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            final long fileSize = entity.getContentLength();
            final String fileName = from.toURL().getFile();
            @Cleanup InputStream inputStream = entity.getContent();
    
            copyToLocal(to, fileName, inputStream, fileSize);
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            throw new IllegalArgumentException();
        } else {
            throw new IllegalStateException(
                    String.format("Unexpected status code (%d) from %s", status, from)
            );
        }
    }

    /**
     * <p>Use digest authentication to download file.
     * @param realm 
     * @param nonce 
     * @see http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientPreemptiveDigestAuthentication.java
     */
    private void downloadViaDigestAuth(URI from, File to, String userName, String password, String realm, String nonce) throws IOException {
        HttpHost targetHost = new HttpHost(from.getHost(), from.getPort(), from.getScheme());
        HttpClientContext localContext = buildClientContextFor(targetHost, realm, nonce);
        HttpGet httpGet = new HttpGet(from);

        @Cleanup
        CloseableHttpClient client = buildHttpClientFor(from, userName, password);
        @Cleanup
        CloseableHttpResponse response = client.execute(targetHost, httpGet, localContext);
        int status = response.getStatusLine().getStatusCode();

        if (status == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();
            final long fileSize = entity.getContentLength();
            final String fileName = from.toURL().getFile();
            @Cleanup InputStream inputStream = entity.getContent();
    
            copyToLocal(to, fileName, inputStream, fileSize);
        } else if (status == HttpStatus.SC_UNAUTHORIZED) {
            throw new IllegalArgumentException();
        } else {
            throw new IllegalStateException(
                    String.format("Unexpected status code (%d) from %s", status, from)
            );
        }
    }

    private CloseableHttpClient buildHttpClientFor(URI from, String userName,
            String password) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(from.getHost(), from.getPort()),
                new UsernamePasswordCredentials(userName, password));

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        return client;
    }

    private HttpClientContext buildClientContextFor(HttpHost targetHost) {
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        return localContext;
    }

    private HttpClientContext buildClientContextFor(HttpHost targetHost, String realm, String nonce) {
        AuthCache authCache = new BasicAuthCache();
        DigestScheme digestAuth = new DigestScheme(); 
        digestAuth.overrideParamter("realm", realm);
        digestAuth.overrideParamter("nonce", nonce);
        authCache.put(targetHost, digestAuth);

        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        return localContext;
    }

}
