package jp.co.worksap.workspace.lombok;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import jp.co.worksap.workspace.common.DownloadFile;
import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.ide.eclipse.EclipseConfiguration;
import jp.co.worksap.workspace.ide.eclipse.EclipseInstaller;
import jp.co.worksap.workspace.ide.eclipse.Version;

import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class LombokInstallerTest {
    private static final String ZIP_FILE_PATH = "./target/empty.zip";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void downloadLombokJar() throws IOException {
        File jarFile = new File("src/test/resources", "lombok-1.12.2.jar");
        if (jarFile.exists()) {
            return;
        }

        URL url = LombokConfiguration.fromString("1.12.4").getUrlToDownload();
        new DownloadFile().download(url, jarFile);
    }

    /**
     * <p>To execute lombok installer, zip file should includes both of eclipse.exe and eclipse.ini</p>
     * @see https://github.com/rzwitserloot/lombok/blob/45f9e9def12b8f32b76c86471487e735ebb7c09b/src/installer/lombok/installer/eclipse/EclipseLocationProvider.java
     */
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
    public void installLombok() throws IOException {
        File eclipseDir = installEclipse();

        LombokConfiguration lombok = LombokConfiguration.fromString("1.12.2");
        new LombokInstaller().install(Optional.of(lombok), eclipseDir);

        List<String> properties = Files.readLines(new File(eclipseDir, "eclipse.ini"), Charsets.UTF_8);
        assertThat(properties,
                // for Windows
                either(contains("-javaagent:lombok.jar", "-Xbootclasspath/a:lombok.jar"))
                // for Linux
                .or(contains("-javaagent:" + eclipseDir.getAbsolutePath() + "/lombok.jar", "-Xbootclasspath/a:" + eclipseDir.getAbsolutePath() + "/lombok.jar")));
    }

    @Test
    public void installLombokWithLocalFile() throws IOException {
        File eclipseDir = installEclipse();

        LombokConfiguration lombok = new LombokConfiguration(null, "src/test/resources/lombok-1.12.2.jar");
        new LombokInstaller().install(Optional.of(lombok), eclipseDir);

        List<String> properties = Files.readLines(new File(eclipseDir, "eclipse.ini"), Charsets.UTF_8);
        assertThat(properties,
                // for Windows
                either(contains("-javaagent:lombok.jar", "-Xbootclasspath/a:lombok.jar"))
                // for Linux
                .or(contains("-javaagent:" + eclipseDir.getAbsolutePath() + "/lombok.jar", "-Xbootclasspath/a:" + eclipseDir.getAbsolutePath() + "/lombok.jar")));
    }

    private File installEclipse() throws IOException {
        Map<OperatingSystem, String> downloadFrom = Maps.newHashMap();
        Version juno = Version.fromString("juno");
        downloadFrom.put(OperatingSystem.create(), ZIP_FILE_PATH);

        EclipseConfiguration configuration = new EclipseConfiguration(juno, null, null, null, downloadFrom, null);
        File targetDir = folder.newFolder();

        EclipseInstaller eclipseInstaller = new EclipseInstaller();
        return eclipseInstaller.install(configuration, targetDir);
    }
}
