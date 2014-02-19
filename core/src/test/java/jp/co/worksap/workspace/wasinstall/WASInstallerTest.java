package jp.co.worksap.workspace.wasinstall;

import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class WASInstallerTest {
    private File wasFixPack;
    private File wasSdkFixPack;
    private File installer;
    private File updateInstaller;

    @Before
    public void ensureFixPacksExist() {
        wasSdkFixPack = new File("src/test/resources", "7.0.0-WS-WASSDK-WinX64-FP0000027.pak");
        wasFixPack = new File("src/test/resources", "7.0.0-WS-WAS-WinX64-FP0000027.pak");
        installer = new File("src/test/resources", "C1G0TML.zip");
        updateInstaller = new File("src/test/resources", "7.0.0.27-WS-UPDI-WinIA32.zip");   // FIXME use 64bit version

        for (File file : ImmutableList.of(wasSdkFixPack, wasFixPack, installer, updateInstaller)) {
            assumeTrue("put " + file.getName() + " to src/test/resources", file.exists());
        }
    }

    @Test
    public void installWAS() {
        List<UpdatePackage> packages = new ArrayList<>();
        packages.add(new UpdatePackage("7.0.0-WS-WASSDK-WinX64-FP0000027", wasSdkFixPack.toURI().toString()));
        packages.add(new UpdatePackage("7.0.0-WS-WAS-WinX64-FP0000027", wasFixPack.toURI().toString()));

        // TODO use temp directory to install
        WASInstallConfiguration configuration = new WASInstallConfiguration("C:\\Program Files\\IBM\\WebSphere", installer.toURI().toString(), updateInstaller.toURI().toString(), packages);
        WASInstaller wasInstaller = new WASInstaller();
        wasInstaller.install(configuration);
    }
}
