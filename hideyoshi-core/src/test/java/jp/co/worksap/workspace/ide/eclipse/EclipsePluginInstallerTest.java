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

import jp.co.worksap.workspace.common.NeverCalledProvider;
import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.download.Downloader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.PatternFilenameFilter;

public class EclipsePluginInstallerTest {
    private static final String INSTALLER_NAME = "eclipse-jee-kepler-SR1-win32-x86_64.zip";
    private static final Pattern CDT_DIR_NAME = Pattern.compile("^org\\.eclipse\\.cdt_.*");

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        Downloader downloader = Downloader.createFor(URI.create(urlToDownload).toURL(), new NeverCalledProvider());
        downloader.download(URI.create(urlToDownload).toURL(), installer);
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
                downloadFrom, null, null);
        File targetDir = folder.newFolder();

        EclipseInstaller eclipseInstaller = new EclipseInstaller();
        File eclipseDir = eclipseInstaller.install(configuration, targetDir, new NeverCalledProvider());
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
