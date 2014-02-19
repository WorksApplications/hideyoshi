package jp.co.worksap.workspace.packagemanagement;

import java.io.IOException;

import jp.co.worksap.workspace.packagemanagement.chocolatey.Chocolatey;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Facade to choose Package Management tools</p>
 * @author Kengo TODA (toda_k@worksap.co.jp)
 */
@Slf4j public class PackageManagementFacade {
    public void install(Iterable<Package> targetPackages) {
        PackageManager manager;
        try {
            manager = createPackageManagerChain();

            log.trace("start installing packages");
            for (Package targetPackage : targetPackages) {
                log.trace("installing {}...", targetPackage);
                manager.install(targetPackage);
                log.trace("installed {}", targetPackage);
            }
            log.trace("finish installing packages");
        } catch (IOException e) {
            throw new IllegalStateException("fail to install package", e);
        }
    }

    private PackageManager createPackageManagerChain() throws IOException {
        PackageManager manager = new AlertingPackageManager();
        if (isOnWindows()) {
            manager = new Chocolatey(manager);
        } else {
            throw new UnsupportedOperationException("Only Windows is supported");
        }
        return manager;
    }

    private boolean isOnWindows() {
        String operatingSystem = System.getProperty("os.name");
        return operatingSystem.startsWith("Windows");
    }
}
