package jp.co.worksap.workspace.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import jp.co.worksap.workspace.packagemanagement.Package;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ConfigurationLoaderTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testEmptyConfiguration() throws IOException {
        File file = folder.newFile();
        Files.write("{\"targetPackages\": []}", file, Charsets.UTF_8);
        Configuration config = new ConfigurationLoader().loadFrom(file);

        assertThat(config.getTargetPackages(), is(empty()));
    }

    @Test
    public void testWithOneTargetPackage() throws IOException {
        File file = folder.newFile();
        Files.write("{\"targetPackages\": [{\"name\":\"wget\"}]}", file, Charsets.UTF_8);
        Configuration config = new ConfigurationLoader().loadFrom(file);

        assertThat(config.getTargetPackages(), contains(Package.of("wget")));
    }

    @Test
    public void testHoconFormat() throws IOException {
        File file = folder.newFile();
        Files.write("targetPackages: [{name:wget}]", file, Charsets.UTF_8);
        Configuration config = new ConfigurationLoader().loadFrom(file);

        assertThat(config.getTargetPackages(), contains(Package.of("wget")));
    }
}
