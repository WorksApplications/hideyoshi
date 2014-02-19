package jp.co.worksap.workspace.packagemanagement;

import java.io.IOException;

import javax.annotation.Nonnull;

public interface PackageManager {
    void install(@Nonnull Package packageToBeInstalled) throws IOException;
}
