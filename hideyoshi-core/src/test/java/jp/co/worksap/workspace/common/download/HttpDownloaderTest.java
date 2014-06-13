package jp.co.worksap.workspace.common.download;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.junit.Test;

import com.google.common.base.Charsets;

public class HttpDownloaderTest {
    private static final String EXPECTED_USER = "user";
    private static final String EXPECTED_PASSWORD = "password";
    private static final String WRONG_PASSWORD = "pazzword";

    @Test
    public void testDownloadSuccessfully() throws IOException {
        InputStream defaultSystemIn = System.in;
        // input username and password by System.in
        System.setIn(new ByteArrayInputStream((EXPECTED_USER + "\n" + EXPECTED_PASSWORD + "\n").getBytes()));
        @Cleanup
        Server server = runServer(10000);

        try {
            File tempFile = File.createTempFile("HttpDownloaderTest", ".txt");
            tempFile.delete();

            new HttpDownloader().download(server.getUri(), tempFile);
            assertThat(tempFile.exists(), is(true));
        } finally {
            System.setIn(defaultSystemIn);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDownloadWithWrongPassword() throws IOException {
        InputStream defaultSystemIn = System.in;
        // input username and password by System.in
        System.setIn(new ByteArrayInputStream((EXPECTED_USER + "\n" + WRONG_PASSWORD + "\n").getBytes()));
        @Cleanup
        Server server = runServer(10001);

        try {
            File tempFile = File.createTempFile("HttpDownloaderTest", ".txt");
            tempFile.delete();

            new HttpDownloader().download(server.getUri(), tempFile);
            assertThat(tempFile.exists(), is(false));
        } finally {
            System.setIn(defaultSystemIn);
        }
    }

    /**
     * @param port TCP port number to listen. Please change this value when you execute this method continuously, it is good to avoid port conflict.
     * @see http://hc.apache.org/httpcomponents-core-4.3.x/httpcore/examples/org/apache/http/examples/ElementalHttpServer.java
     */
    private Server runServer(int port) throws UnknownHostException {
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("HttpDownloaderTest/1.0"))
                .add(new ResponseContent())
                .add(new ResponseConnControl()).build();
        UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper();
        reqistry.register("*", new BasicRequestHandler());
        HttpService service = new HttpService(httpproc, reqistry);
        RequestListener listener = new RequestListener(service, port);

        Thread thread = new Thread(listener);
        thread.setName("Server to test HttpDownloader");
        thread.setDaemon(true);
        thread.start();

        return new Server(URI.create("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port + "/"), listener);
    }

    private static final class BasicRequestHandler implements
            HttpRequestHandler {
        @Override
        public void handle(HttpRequest request, HttpResponse response,
                HttpContext context) throws HttpException, IOException {
            Header auth = request.getFirstHeader("Authorization");
            if (auth == null) {
                response.setHeader(new BasicHeader("WWW-Authenticate", "Basic realm=\"realm\""));
                response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
                return;
            }

            String value = auth.getValue();
            if (! value.startsWith("Basic ")) {
                response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                return;
            }

            String encodedUserAndPass = value.substring("Basic ".length());
            byte[] userAndPass = Base64.decodeBase64(encodedUserAndPass.getBytes(Charsets.UTF_8));
            if (new String(userAndPass, Charsets.UTF_8).equals(EXPECTED_USER + ":" + EXPECTED_PASSWORD)) {
                response.setStatusCode(HttpStatus.SC_OK);
            } else {
                response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            }
        }
    }

    @AllArgsConstructor
    @Slf4j
    private static class RequestListener implements Runnable {
        @Nonnull
        private final HttpService service;
        @Nonnull
        private final int port;
        @Nonnull
        private final AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void run() {
            try {
                @Cleanup ServerSocket serverSocket = new ServerSocket(port, 10, InetAddress.getLocalHost());
                log.info("Server is listening on: {}", serverSocket.getInetAddress().toString());
                DefaultBHttpServerConnectionFactory connectionFactory = DefaultBHttpServerConnectionFactory.INSTANCE;
                while (running.get() && !Thread.interrupted()) {
                    Socket socket = serverSocket.accept();
                    HttpServerConnection connection = connectionFactory.createConnection(socket);
                    HttpContext context = new BasicHttpContext(null);
                    service.handleRequest(connection, context);
                }
            } catch (IOException | HttpException e) {
                throw new RuntimeException(e);
            }
        }

        public void stop() {
            running.set(false);
        }
    }

    @Value
    private static final class Server implements Closeable {
        @Nonnull
        private final URI uri;
        @Nonnull
        private final RequestListener listener;

        @Override
        public void close() {
            listener.stop();
        }
    }
}
