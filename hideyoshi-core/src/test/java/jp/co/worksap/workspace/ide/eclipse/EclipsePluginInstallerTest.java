package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
import java.util.regex.Pattern;

import jp.co.worksap.workspace.common.DownloadFile;
import jp.co.worksap.workspace.common.OperatingSystem;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.PatternFilenameFilter;

public class EclipsePluginInstallerTest {
    private static final String INSTALLER_NAME = "eclipse-jee-kepler-SR1-win32-x86_64.zip";
    private static final String ZIP_FILE_PATH = "./target/empty.zip";
    private static final Pattern CDT_DIR_NAME = Pattern.compile("^org\\.eclipse\\.cdt_.*");

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void makeZipFile() throws ZipException, IOException {
        ZipFile zipFile = new ZipFile(new File(ZIP_FILE_PATH));
        zipFile.getFile().delete();
        File iniFile = folder.newFile("eclipse.ini");
        File exeFile = folder.newFile("eclipse.exe");
        zipFile.addFile(iniFile, new ZipParameters());
        zipFile.addFile(exeFile, new ZipParameters());
    }

    @Before
    public void ensureWindows64bit() {
        OperatingSystem os = OperatingSystem.create();
        assumeThat(os, is(equalTo(OperatingSystem.WIN64)));
    }

    @Before
    public void ensureInstallerExists() throws MalformedURLException, IOException {
        File installer = new File("src/test/resources", INSTALLER_NAME);
        if (installer.exists()) {
            return;
        }

        Version keplerSr1 = Version.fromString("4.3.1");
        String urlToDownload = new DownloadUrlFinder().findDownloadUrl(keplerSr1);
        new DownloadFile().download(URI.create(urlToDownload).toURL(), installer);
    }

    @Test
    public void installPlugin() throws IOException {
        assumeThat(OperatingSystem.create(), is(OperatingSystem.fromString("win.64")));

        Map<OperatingSystem, String> downloadFrom = Maps.newHashMap();
        downloadFrom.put(OperatingSystem.fromString("win.64"), "./src/test/resources/" + INSTALLER_NAME);
        Version juno = Version.fromString("kepler");

        EclipseConfiguration configuration = new EclipseConfiguration(juno, null,
                Lists.newArrayList(EclipsePlugin.of("org.eclipse.cdt.feature.group", "8.2.1.201309180223")),
                Lists.newArrayList("http://download.eclipse.org/tools/cdt/releases/kepler"),
                downloadFrom);
        File targetDir = folder.newFolder();

        EclipseInstaller eclipseInstaller = new EclipseInstaller();
        File eclipseDir = eclipseInstaller.install(configuration, targetDir);
        assertThatCdtHasBeenInstalled(eclipseDir, false);

        EclipsePluginInstaller pluginInstaller = new EclipsePluginInstaller();
        pluginInstaller.install(configuration, targetDir);

        assertThatCdtHasBeenInstalled(eclipseDir, true);
    }

    private void assertThatCdtHasBeenInstalled(File eclipseDir, boolean expect) {
        File features = new File(eclipseDir, "features");
        features.mkdir();
        boolean installed = features.list(new PatternFilenameFilter(CDT_DIR_NAME)).length > 0;
        assertEquals(expect, installed);
    }
}
