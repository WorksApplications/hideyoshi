package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.extern.slf4j.Slf4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

@Slf4j
@ParametersAreNonnullByDefault
class JavaFormatterConfigurator {
    private static final String METADATA_DIRECTORY = ".metadata/.plugins/org.eclipse.core.runtime/.settings";
    private static final String METADATA_FILE = "org.eclipse.jdt.core.prefs";
    private static final String PREFIX_OF_PARAMETER = "org.eclipse.jdt.core.formatter.";

    void configure(Optional<JavaFormatterConfiguration> configuration, File workspace) {
        if (! configuration.isPresent()) {
            log.info("no configuration provided. skip configuring Java formatter.");
            return;
        }

        String sourcePath = configuration.get().getMetadata();
        if (Strings.isNullOrEmpty(sourcePath)) {
            log.info("no metadata file specified. skip configuring Java formatter.");
            return;
        }

        File sourceMetadata = new File(sourcePath);
        File metadataFile = metadataIn(workspace);
        if (! sourceMetadata.isFile()) {
            log.warn("source of metadata file does not exist: {}", sourceMetadata.getAbsolutePath());
            return;
        }

        addLines(sourceMetadata, metadataFile);
    }

    /**
     * Replace lines in current metadata with configuration.
     * Lines which starts with PREFIX_OF_PARAMETER is the target.
     */
    @VisibleForTesting
    void addLines(File configuration, File existingMetadataFile) {
        PrefixFilter prefixFilter = new PrefixFilter();
        Charset configurationFileCharset = Charsets.UTF_8;

        try {
            FluentIterable<String> existingOtherMetadata;
            if (existingMetadataFile.exists()) {
                existingOtherMetadata = FluentIterable.from(Files.readLines(existingMetadataFile, configurationFileCharset))
                        .filter(Predicates.not(prefixFilter));
            } else {
                existingOtherMetadata = FluentIterable.from(new ArrayList<String>());
            }
            FluentIterable<String> givenConfiguartion = FluentIterable.from(Files.readLines(configuration, configurationFileCharset))
                    .filter(prefixFilter);

            Iterable<String> newConfiguration =
                    Iterables.concat(existingOtherMetadata, givenConfiguartion);
            Files.asCharSink(existingMetadataFile, configurationFileCharset).writeLines(newConfiguration);
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

    private static final class PrefixFilter implements Predicate<String> {
        @Override
        public boolean apply(String string) {
            return string.startsWith(PREFIX_OF_PARAMETER);
        }
    }
}
