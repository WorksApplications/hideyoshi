package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.regex.Pattern;

import jp.co.worksap.workspace.common.OperatingSystem;

import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;

public class EclipseInstallerTest {
    private static final String ZIP_FILE_PATH = "./target/empty.zip";
    private static final Pattern M2EWTP_DIR_NAME = Pattern.compile("^org\\.eclipse\\.m2e\\.wtp\\.feature_.*");

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void makeZipFile() throws IOException {
        ZipArchiver archiver = new ZipArchiver();
        archiver.setDestFile(new File(ZIP_FILE_PATH));
        archiver.getDestFile().delete();

        File eclipseDir = folder.newFolder("eclipse");
        File iniFile = new File(eclipseDir, "eclipse.ini");
        File exeFile = new File(eclipseDir, "eclipse.exe");
        Files.touch(iniFile);
        Files.touch(exeFile);
        archiver.addDirectory(eclipseDir);
        archiver.createArchive();
    }

    @Test
    public void installJuno() throws IOException {
        Map<OperatingSystem, String> downloadFrom = Maps.newHashMap();
        Version juno = Version.fromString("juno");
        downloadFrom.put(OperatingSystem.create(), ZIP_FILE_PATH);

        EclipseConfiguration configuration = new EclipseConfiguration(juno, null, null, null, downloadFrom, null);
        File targetDir = folder.newFolder();

        EclipseInstaller eclipseInstaller = new EclipseInstaller();
        File eclipseDir = eclipseInstaller.install(configuration, targetDir);
        assertThat(eclipseDir, is(not(equalTo(targetDir))));
        assertThat(eclipseDir, is(equalTo(new File(targetDir, "eclipse"))));
        assertTrue(new File(eclipseDir, "eclipse.ini").exists());
        assertThatM2eWtpHasBeenInstalled(targetDir, false);
    }

    @Test
    public void convertFilePathToDownloadUrl() throws MalformedURLException, IOException {
        Map<OperatingSystem, String> downloadFrom = Maps.newHashMap();
        Version juno = Version.fromString("juno");
        downloadFrom.put(OperatingSystem.create(), ZIP_FILE_PATH);
        String simplePath = simplePathOf(ZIP_FILE_PATH);

        EclipseConfiguration configuration = new EclipseConfiguration(juno, null, null, null, downloadFrom, null);
        EclipseInstaller eclipseInstaller = new EclipseInstaller();
        assertThat(eclipseInstaller.findDownloadUrl(configuration), is(new File(simplePath).toURI().toURL().toString()));
    }

    @Test
    public void installShouldBeSkippedIfEclipseDirExists() throws IOException {
        Map<OperatingSystem, String> downloadFrom = Maps.newHashMap();
        Version juno = Version.fromString("juno");
        downloadFrom.put(OperatingSystem.create(), ZIP_FILE_PATH);

        EclipseConfiguration configuration = new EclipseConfiguration(juno, null,
                Lists.newArrayList(EclipsePlugin.of("egit")),
                Lists.newArrayList("http://download.eclipse.org/egit/updates/"),
                downloadFrom, null);
        File targetDir = folder.newFolder();

        // generate directory: installer should find it and skip installation
        new File(targetDir, "eclipse").mkdir();

        EclipseInstaller eclipseInstaller = new EclipseInstaller();
        eclipseInstaller.install(configuration, targetDir);
        assertFalse(new File(targetDir, "eclipse.ini").exists());
    }

    private String simplePathOf(String relativePath) {
        return Files.simplifyPath(new File(relativePath).getAbsolutePath().replace('\\', '/'));
    }

    private void assertThatM2eWtpHasBeenInstalled(File location, boolean expect) {
        File features = new File(location, "features");
        features.mkdir();
        boolean installed = features.list(new PatternFilenameFilter(M2EWTP_DIR_NAME)).length > 0;
        assertEquals(expect, installed);
    }
}
