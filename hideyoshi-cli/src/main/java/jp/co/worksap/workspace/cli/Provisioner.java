package jp.co.worksap.workspace.cli;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import jp.co.worksap.workspace.database.db2.DB2Configuration;
import jp.co.worksap.workspace.database.db2.DB2Installer;
import jp.co.worksap.workspace.ide.eclipse.EclipseConfiguration;
import jp.co.worksap.workspace.ide.eclipse.EclipseInstaller;
import jp.co.worksap.workspace.ide.eclipse.EclipsePluginInstaller;
import jp.co.worksap.workspace.wasprofile.WebSphereConfiguration;
import jp.co.worksap.workspace.wasprofile.CommonWASConfiguration;
import jp.co.worksap.workspace.wasprofile.CreateProfileConfiguration;
import jp.co.worksap.workspace.wasprofile.SharedLibraryConfiguration;
import jp.co.worksap.workspace.wasprofile.JDBCProviderConfiguration;
import jp.co.worksap.workspace.wasprofile.GlobalSecurityConfigurationContainer;
import jp.co.worksap.workspace.wasprofile.CommonDSConfiguration;
import jp.co.worksap.workspace.wasprofile.DataSourcesConfigurationContainer;
import jp.co.worksap.workspace.wasprofile.JVMHeapSizeConfiguration;
import jp.co.worksap.workspace.lombok.LombokConfiguration;
import jp.co.worksap.workspace.lombok.LombokInstaller;
import jp.co.worksap.workspace.packagemanagement.Package;
import jp.co.worksap.workspace.packagemanagement.PackageManagementFacade;
import jp.co.worksap.workspace.wasinstall.WASInstallConfiguration;
import jp.co.worksap.workspace.wasinstall.WASInstaller;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class Provisioner {
    private final PackageManagementFacade packageManagerFacade;
    private final EclipseInstaller eclipseInstaller;
    private final EclipsePluginInstaller eclipsePluginInstaller;
    private final LombokInstaller lombokInstaller;
    private final DB2Installer db2Installer;
    private final WASInstaller wasInstaller;
    private final WebSphereConfiguration wasConfigure;

    Provisioner(PackageManagementFacade packageManagerFacade, EclipseInstaller eclipseInstaller, EclipsePluginInstaller eclipsePluginInstaller, LombokInstaller lombokInstaller, DB2Installer db2Installer, WASInstaller wasInstaller, WebSphereConfiguration wasConfigure) {
        this.packageManagerFacade = checkNotNull(packageManagerFacade);
        this.eclipseInstaller = checkNotNull(eclipseInstaller);
        this.eclipsePluginInstaller = checkNotNull(eclipsePluginInstaller);
        this.lombokInstaller = checkNotNull(lombokInstaller);
        this.db2Installer = checkNotNull(db2Installer);
        this.wasInstaller = checkNotNull(wasInstaller);
        this.wasConfigure = checkNotNull(wasConfigure);
    }

    StatusCode execute(Configuration configuration) throws IOException {
        File targetLocation = configuration.getTargetLocation();
        if (!targetLocation.isDirectory()) {
            log.error("targetLocation should be existed directory: " + targetLocation.getAbsolutePath());
            return StatusCode.ERROR;
        }

        try {
            installPackages(configuration);
            installEclipseAndPlugin(configuration, targetLocation);
            installDB2(configuration);
            installWAS(configuration);
            configureWebsphere(configuration);
            return StatusCode.NORMAL;
        } catch (RuntimeException e) {
            log.error("fail to provision", e);
            return StatusCode.ERROR;
        }
    }

    private void installEclipseAndPlugin(Configuration configuration, File targetLocation) {
        EclipseConfiguration eclipseConfiguration = configuration.getEclipse();
        if (eclipseConfiguration != null) {
            File eclipseDir = eclipseInstaller.install(eclipseConfiguration, targetLocation);
            eclipsePluginInstaller.install(eclipseConfiguration, eclipseDir);
            LombokConfiguration lombok = configuration.getLombok();
            if (configuration.getLombok() != null) {
                lombokInstaller.install(Optional.fromNullable(lombok), eclipseDir);
            }
        } else if (configuration.getLombok() != null) {
            log.warn("you need Eclipse configuration to set up lombok");
        } else {
            log.info("no Eclipse is required");
        }
    }

    private void installPackages(Configuration configuration) {
        Iterable<Package> targetPackages = configuration.getTargetPackages();
        if (targetPackages != null && !Iterables.isEmpty(targetPackages)) {
            packageManagerFacade.install(targetPackages);
        } else {
            log.info("no package is required");
        }
    }

    private void installDB2(Configuration configuration) {
        DB2Configuration db2Configuration = configuration.getDb2();
        if (db2Configuration != null) {
            db2Installer.install(db2Configuration);
        } else {
            log.info("no DB2 is required");
        }
    }
    
    public void configureWebsphere(Configuration configuration) throws IOException{        
        CreateProfileConfiguration wasProfile = configuration.getWasProfileConfig();
        CommonWASConfiguration commonWASConfig = configuration.getWasCommonConfig();
        SharedLibraryConfiguration slConfig = configuration.getSharedLibraryConfig();
        JDBCProviderConfiguration jdbcConfig = configuration.getJdbcProviderConfig();
        GlobalSecurityConfigurationContainer gsConfig = configuration.getGlobalSecurityConfig();
        CommonDSConfiguration commonDSConfig = configuration.getDataSourcesCommonConfig();
        DataSourcesConfigurationContainer dsConfig = configuration.getDataSourcesConfig();
        JVMHeapSizeConfiguration jvmConfig = configuration.getJvmHeapSizeConfig();     
        if (wasProfile!=null && slConfig!=null && jdbcConfig!=null && gsConfig!=null && dsConfig!=null && jvmConfig!=null) {            
            wasConfigure.createAndConfigureProfile(commonWASConfig, wasProfile, slConfig, jdbcConfig, gsConfig, commonDSConfig, dsConfig, jvmConfig);            
        }
        else {
            log.info("no WebSphere Configuration is required");
        } 
    }


    private void installWAS(Configuration configuration) {
        WASInstallConfiguration wasInstallConfiguration = configuration.getWasInstall();
        if (wasInstallConfiguration != null) {
            wasInstaller.install(wasInstallConfiguration);
        } else {
            log.info("no DB2 is required");
        }
    }

}
