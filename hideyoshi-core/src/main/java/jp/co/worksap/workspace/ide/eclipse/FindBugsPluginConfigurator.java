package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.extern.slf4j.Slf4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.Files;

/**
 * Note that FindBugs plugin requires Eclipse version 3.6 or later.
 * http://findbugs.sourceforge.net/FAQ.html
 */
@Slf4j
@ParametersAreNonnullByDefault
class FindBugsPluginConfigurator {
    private static final String METADATA_DIRECTORY = ".metadata/.plugins/org.eclipse.core.runtime/.settings";
    private static final String METADATA_FILE = "edu.umd.cs.findbugs.plugin.eclipse.prefs";

    void configure(Optional<FindBugsPluginConfiguration> configuration, File workspace) {
        if (! configuration.isPresent()) {
            log.info("no configuration provided. skip configuring FindBugs.");
            return;
        }

        String sourcePath = configuration.get().getMetadata();
        if (Strings.isNullOrEmpty(sourcePath)) {
            log.info("no metadata file specified. skip configuring FindBugs.");
            return;
        }

        File sourceMetadata = new File(sourcePath);
        File metadataFile = metadataIn(workspace);
        if (! sourceMetadata.isFile()) {
            log.warn("source of metadata file does not exist: {}", sourceMetadata.getAbsolutePath());
            return;
        }
        copy(sourceMetadata, metadataFile);
    }

    @VisibleForTesting
    void copy(File sourceMetadata, File metadataFile) {
        try {
            Files.copy(sourceMetadata, metadataFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private File metadataIn(File workspace) {
        File metadataDirectory = new File(workspace, METADATA_DIRECTORY);
        if (! metadataDirectory.isDirectory()) {
            metadataDirectory.mkdirs();
        }

        return new File(metadataDirectory, METADATA_FILE);
    }
}
