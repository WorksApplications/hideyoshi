package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class EclipseWorkspaceInitializerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private EclipseWorkspaceInitializer initializer;
    private File workspace;
    private File settingDirectory;
    private File prefFile;

    @Before
    public void setUp() throws IOException {
        initializer = new EclipseWorkspaceInitializer();
        workspace = folder.newFolder();
        settingDirectory = new File(workspace, ".settings");
        prefFile = new File(settingDirectory, "org.eclipse.core.resources.prefs");

        assertThat(settingDirectory.exists(), is(false));
        assertThat(prefFile.exists(), is(false));
    }

    @Test
    public void workspaceInitializationShouldCreatePrefFile() throws IOException {
        initializer.initialize(new EclipseConfiguration(), workspace);

        assertThat(settingDirectory.isDirectory(), is(true));
        assertThat(prefFile.isFile(), is(true));
    }

    @Test
    public void workspaceInitializationUsesProperEncoding() throws IOException {
        EclipseConfiguration configuration = Mockito.spy(new EclipseConfiguration());
        doReturn(Charset.forName("SHIFT_JIS")).when(configuration).getDefaultCharset();

        initializer.initialize(configuration, workspace);
        assertThat(findCharset(), is(equalToIgnoringCase("SHIFT_JIS")));
    }

    private String findCharset() throws IOException {
        for (String line : Files.readLines(prefFile, Charsets.UTF_8)) {
            if (line.startsWith(EclipseWorkspaceInitializer.PREFIX_OF_ENCODING)) {
                return line.substring(EclipseWorkspaceInitializer.PREFIX_OF_ENCODING.length());
            }
        }
        throw new IllegalStateException("no encoding found");
    }
}
