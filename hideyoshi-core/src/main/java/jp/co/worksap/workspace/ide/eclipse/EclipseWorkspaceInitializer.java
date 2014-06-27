package jp.co.worksap.workspace.ide.eclipse;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import lombok.AllArgsConstructor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class EclipseWorkspaceInitializer {
    @VisibleForTesting
    static final String PREFIX_OF_ENCODING = "encoding/<project>=";

    /**
     * @return directory which is workspace of Eclipse
     */
    public void initialize(EclipseConfiguration config, File workspace) {
        ensureDirectoryExists(workspace);

        try {
            createPrefFiles(workspace, config.getDefaultCharset());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        new FindBugsPluginConfigurator().configure(config.getFindbugs(), workspace);
        new JavaFormatterConfigurator().configure(config.getJavaFormat(), workspace);
    }

    private void createPrefFiles(File workspace, Charset defaultCharset) throws IOException {
        File settingDirectory = new File(workspace, ".settings");
        ensureDirectoryExists(settingDirectory);

        URL template = Resources.getResource(getClass(), "org.eclipse.core.resources.prefs");
        File corePrefFile = new File(settingDirectory, "org.eclipse.core.resources.prefs");

        FluentIterable<String> prefs = FluentIterable.from(Resources.asCharSource(template, Charsets.UTF_8).readLines());
        Files.asCharSink(corePrefFile, Charsets.UTF_8).writeLines(
                prefs.transform(new EncodingReplacer(defaultCharset)));
    }

    private void ensureDirectoryExists(File directory) {
        if (!directory.isDirectory() && !directory.mkdir()) {
            throw new IllegalStateException("could not create directory at:" + directory.getAbsolutePath());
        }
    }

    @AllArgsConstructor
    private static final class EncodingReplacer implements Function<String, String> {
        private final Charset charset;

        @Override
        public String apply(String before) {
            checkNotNull(before);

            if (before.startsWith(PREFIX_OF_ENCODING)) {
                return PREFIX_OF_ENCODING + charset.displayName();
            } else {
                return before;
            }
        }

    }
}
