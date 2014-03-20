package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Files;
import com.google.common.io.Resources;

public class EclipseWorkspaceInitializer {
    /**
     * @return directory which is workspace of Eclipse
     */
    public void initialize(EclipseConfiguration config, File workspace) {
        ensureDirectoryExists(workspace);

        try {
            createPrefFiles(workspace);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        new FindBugsPluginConfigurator().configure(config.getFindbugs(), workspace);
    }

    private void createPrefFiles(File workspace) throws IOException {
        File settingDirectory = new File(workspace, ".settings");
        ensureDirectoryExists(settingDirectory);

        URL template = Resources.getResource(getClass(), "org.eclipse.core.resources.prefs");
        File corePrefFile = new File(settingDirectory, "org.eclipse.core.resources.prefs");

        // TODO support changing encoding from UTF-8 to required one
        Resources.asByteSource(template).copyTo(Files.asByteSink(corePrefFile));
    }

    private void ensureDirectoryExists(File directory) {
        if (!directory.isDirectory() && !directory.mkdir()) {
            throw new IllegalStateException("could not create directory at:" + directory.getAbsolutePath());
        }
    }
}
