package jp.co.worksap.workspace.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class UrlCreatorTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File workingDir;

    @Before
    public void setUp() throws IOException {
        workingDir = folder.newFolder();
    }

    @Test
    public void testAbsoluteUri() throws MalformedURLException {
        UrlCreator creator = new UrlCreator();
        assertThat(
                creator.createFrom(URI.create("http://localhost:8080/context/"), workingDir),
                is(new URL("http://localhost:8080/context/")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpaqueUri() throws MalformedURLException {
        UrlCreator creator = new UrlCreator();
        creator.createFrom(URI.create("mailto:foo@bar.baz"), workingDir);
    }

    @Test
    public void testRelativeUri() throws MalformedURLException {
        UrlCreator creator = new UrlCreator();
        assertThat(
                creator.createFrom(URI.create("context"), workingDir),
                is(new File(workingDir, "context").toURI().toURL()));
    }

}
