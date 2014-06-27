package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
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

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;

public class JavaFormatterConfiguratorTest {
    private static final String LINE_SHOULD_BE_IGNORED = "this line will be ignored, because it has no proper prefix.";
    private static final String METADATA_FILE = "org.eclipse.jdt.core.prefs";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private JavaFormatterConfigurator configurator;
    private File workspace;

    @Before
    public void setUp() throws IOException {
        configurator = spy(new JavaFormatterConfigurator());
        workspace = folder.newFolder();
    }

    @Test
    public void skipExecutionWhenNoConfigurationIsProvided() {
        configurator.configure(Optional.fromNullable((JavaFormatterConfiguration) null), workspace);
        verify(configurator, never()).addLines(any(File.class), any(File.class));
    }

    @Test
    public void skipExecutionWhenConfigurationDoesNotExist() {
        JavaFormatterConfiguration configuration = new JavaFormatterConfiguration("does-not-exist");
        configurator.configure(Optional.fromNullable(configuration), workspace);
        verify(configurator, never()).addLines(any(File.class), any(File.class));
    }

    @Test
    public void testPluginCreatesMetadataFile() throws IOException {
        File source = new File("src/test/resources", METADATA_FILE);
        JavaFormatterConfiguration configuration = new JavaFormatterConfiguration(source.getAbsolutePath());
        File metadata = new File(workspace, ".metadata/.plugins/org.eclipse.core.runtime/.settings/" + METADATA_FILE);

        assertThat(workspace.listFiles().length, is(0));
        assertThat(metadata.isFile(), is(false));
        assertTrue(FluentIterable.from(Files.readLines(source, Charsets.UTF_8)).contains(LINE_SHOULD_BE_IGNORED));

        configurator.configure(Optional.of(configuration), workspace);

        assertThat(workspace.listFiles().length, is(1));
        assertThat(workspace.listFiles()[0].getName(), is(".metadata"));
        assertThat(metadata.isFile(), is(true));
        assertThat(Files.equal(metadata, source), is(false));   // because this feature filter lines which has no proper prefix
        assertFalse(FluentIterable.from(Files.readLines(metadata, Charsets.UTF_8)).contains(LINE_SHOULD_BE_IGNORED));
    }

    @Test
    public void testPluginAppendLinesToExistingMetadataFile() throws IOException {
        File source = new File("src/test/resources", METADATA_FILE);
        JavaFormatterConfiguration configuration = new JavaFormatterConfiguration(source.getAbsolutePath());
        File metadata = new File(workspace, ".metadata/.plugins/org.eclipse.core.runtime/.settings/" + METADATA_FILE);
        metadata.getParentFile().mkdirs();
        Files.copy(new File("src/test/resources", "existing-" + METADATA_FILE), metadata);
        String oldTabulationSize = FluentIterable.from(Files.readLines(metadata, Charsets.UTF_8)).firstMatch(new TabulationSizePredicator()).get();

        assertThat(workspace.listFiles().length, is(1));
        assertThat(workspace.listFiles()[0].getName(), is(".metadata"));
        assertThat(metadata.isFile(), is(true));

        configurator.configure(Optional.of(configuration), workspace);

        assertThat(workspace.listFiles().length, is(1));
        assertThat(workspace.listFiles()[0].getName(), is(".metadata"));
        assertThat(metadata.isFile(), is(true));
        assertThat(Files.equal(metadata, source), is(false));
        assertFalse(FluentIterable.from(Files.readLines(metadata, Charsets.UTF_8)).contains(LINE_SHOULD_BE_IGNORED));

        String newTabulationSize = FluentIterable.from(Files.readLines(metadata, Charsets.UTF_8)).firstMatch(new TabulationSizePredicator()).get();
        assertTrue(oldTabulationSize.endsWith("4"));
        assertTrue(newTabulationSize.endsWith("2"));
    }

    private static final class TabulationSizePredicator implements Predicate<String> {
        @Override
        public boolean apply(String line) {
            return line.startsWith("org.eclipse.jdt.core.formatter.tabulation.size=");
        }
    }
}
