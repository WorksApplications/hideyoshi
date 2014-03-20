package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class EclipseWorkspaceInitializerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void workspaceInitializationShouldCreatePrefFile() throws IOException {
        EclipseWorkspaceInitializer initializer = new EclipseWorkspaceInitializer();
        File workspace = folder.newFolder();
        File settingDirectory = new File(workspace, ".settings");
        File prefFile = new File(settingDirectory, "org.eclipse.core.resources.prefs");

        assertThat(settingDirectory.exists(), is(false));
        assertThat(prefFile.exists(), is(false));
        initializer.initialize(new EclipseConfiguration(), workspace);
        assertThat(settingDirectory.isDirectory(), is(true));
        assertThat(prefFile.isFile(), is(true));
    }
}
