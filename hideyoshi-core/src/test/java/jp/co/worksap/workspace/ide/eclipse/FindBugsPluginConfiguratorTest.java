package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Optional;
import com.google.common.io.Files;

public class FindBugsPluginConfiguratorTest {
    private static final String METADATA_FILE = "edu.umd.cs.findbugs.plugin.eclipse.prefs";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private FindBugsPluginConfigurator configurator;
    private File workspace;

    @Before
    public void setUp() throws IOException {
        configurator = spy(new FindBugsPluginConfigurator());
        workspace = folder.newFolder();
    }

    @Test
    public void skipExecutionWhenNoConfigurationIsProvided() {
        configurator.configure(Optional.fromNullable((FindBugsPluginConfiguration) null), workspace);
        verify(configurator, never()).copy(any(File.class), any(File.class));
    }

    @Test
    public void skipExecutionWhenConfigurationDoesNotExist() {
        FindBugsPluginConfiguration configuration = new FindBugsPluginConfiguration("does-not-exist");
        configurator.configure(Optional.fromNullable(configuration), workspace);
        verify(configurator, never()).copy(any(File.class), any(File.class));
    }

    @Test
    public void testPluginCreatesMetadataFile() throws IOException {
        File source = new File("src/test/resources", METADATA_FILE);
        FindBugsPluginConfiguration configuration = new FindBugsPluginConfiguration(source.getAbsolutePath());
        File metadata = new File(workspace, ".metadata/.plugins/org.eclipse.core.runtime/.settings/" + METADATA_FILE);

        assertThat(workspace.listFiles().length, is(0));
        assertThat(metadata.isFile(), is(false));

        configurator.configure(Optional.of(configuration), workspace);

        assertThat(workspace.listFiles().length, is(1));
        assertThat(workspace.listFiles()[0].getName(), is(".metadata"));
        assertThat(metadata.isFile(), is(true));
        assertThat(Files.equal(metadata, source), is(true));
    }

}
