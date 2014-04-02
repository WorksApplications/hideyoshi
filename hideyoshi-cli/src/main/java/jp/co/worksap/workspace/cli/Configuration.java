package jp.co.worksap.workspace.cli;

import java.io.File;
import java.util.List;
import java.util.Map;

import jp.co.worksap.workspace.database.db2.DB2Configuration;
import jp.co.worksap.workspace.ide.eclipse.EclipseConfiguration;
import jp.co.worksap.workspace.lombok.LombokConfiguration;
import jp.co.worksap.workspace.packagemanagement.Package;
import jp.co.worksap.workspace.repository.git.GitHookConfiguration;
import jp.co.worksap.workspace.repository.git.GitRepositoryConfiguration;
import jp.co.worksap.workspace.wasinstall.WASInstallConfiguration;
import jp.co.worksap.workspace.wasprofile.ProfileConfiguration;
import jp.co.worksap.workspace.wasprofile.JDBCProviderConfiguration;
import jp.co.worksap.workspace.wasprofile.SharedLibraryConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
class Configuration {
    private File targetLocation = new File(".");
    private List<Package> targetPackages;
    private EclipseConfiguration eclipse;
    private LombokConfiguration lombok;
    private DB2Configuration db2;
    private ProfileConfiguration wasProfileConfig;
    private SharedLibraryConfiguration sharedLibraryConfig;
    private JDBCProviderConfiguration jdbcProviderConfig;
    private WASInstallConfiguration wasInstall;
    private ProfileConfiguration wasProfile;
    private SharedLibraryConfiguration slConfig;
    private JDBCProviderConfiguration jdbcConfig;
    private Map<String, GitRepositoryConfiguration> repository;
    private Map<String, GitHookConfiguration> gitHook;
}
