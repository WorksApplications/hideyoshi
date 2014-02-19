package jp.co.worksap.workspace.packagemanagement;

import org.junit.Test;

public class AlertingPackageManagerTest {

    @Test(expected=FailToInstallException.class)
    public void installShouldThrowException() {
        Package packageToBeInstalled = new Package("dummy", "1.0.0");
        new AlertingPackageManager().install(packageToBeInstalled);
    }

}
