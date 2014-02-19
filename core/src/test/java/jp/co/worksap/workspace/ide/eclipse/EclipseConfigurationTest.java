package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import jp.co.worksap.workspace.common.OperatingSystem;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class EclipseConfigurationTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadVersion() throws IOException {
        File file = folder.newFile();
        Files.write("{\"version\": \"4.3.0\"}", file, Charsets.UTF_8);
        EclipseConfiguration config = new ObjectMapper().readValue(file, EclipseConfiguration.class);

        assertThat(config.getVersion(), is(Version.fromString("4.3.0")));
    }

    @Test
    public void loadDefaultCharset() throws IOException {
        File file = folder.newFile();
        Files.write("{\"version\": \"4.3.0\", \"defaultCharset\": \"SHIFT_JIS\"}", file, Charsets.UTF_8);
        EclipseConfiguration config = new ObjectMapper().readValue(file, EclipseConfiguration.class);

        assertThat(config.getDefaultCharset(), is(Charset.forName("SHIFT_JIS")));
    }

    @Test
    public void loadPlugin() throws IOException {
        File file = folder.newFile();
        Files.write("{\"version\": \"4.3.0\", \"plugin\": [\"foo\"]}", file, Charsets.UTF_8);
        EclipseConfiguration config = new ObjectMapper().readValue(file, EclipseConfiguration.class);

        assertThat(config.getPlugin(), is(contains(EclipsePlugin.of("foo"))));
    }

    @Test
    public void loadPluginRepository() throws IOException {
        File file = folder.newFile();
        Files.write("{\"version\": \"4.3.0\", \"pluginRepository\": [\"http://download.eclipse.org/egit/updates/\"]}", file, Charsets.UTF_8);
        EclipseConfiguration config = new ObjectMapper().readValue(file, EclipseConfiguration.class);

        assertThat(config.getPluginRepository(), is(contains("http://download.eclipse.org/egit/updates/")));
    }

    @Test
    public void loadDownloadSource() throws IOException {
        File file = folder.newFile();
        Files.write("{\"version\": \"4.3.0\", \"downloadFrom\": {\"win.32\": \"http://server/eclipse-4.3.0.zip\"}}", file, Charsets.UTF_8);
        EclipseConfiguration config = new ObjectMapper().readValue(file, EclipseConfiguration.class);

        Map<OperatingSystem, String> expected = Maps.newHashMap();
        expected.put(OperatingSystem.fromString("win.32"), "http://server/eclipse-4.3.0.zip");
        assertThat(config.getDownloadFrom(), is(expected));
    }
}
