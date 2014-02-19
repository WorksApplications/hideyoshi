package jp.co.worksap.workspace.packagemanagement;

import javax.annotation.Nonnull;


/**
 * <p>A package manager which will be put at the end of chain-of-responsibility.</p>
 * <p>When some package reaches to this package manager, it means that we cannot
 * find suitable package manager. This class will throw exception to tell this problem.</p>
 *
 * @author Kengo TODA (toda_k@worksap.co.jp)
 * @see http://en.wikipedia.org/wiki/Chain-of-responsibility_pattern
 */
public class AlertingPackageManager implements PackageManager {

    @Override
    public void install(@Nonnull Package packageToBeInstalled) {
        throw new FailToInstallException(String.format(
                "Cannot find suitable package manager for %s:%s",
                packageToBeInstalled.getName(),
                packageToBeInstalled.getVersion()));
    }

}
