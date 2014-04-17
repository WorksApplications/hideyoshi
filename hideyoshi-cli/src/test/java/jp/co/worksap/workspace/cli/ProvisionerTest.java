package jp.co.worksap.workspace.cli;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.co.worksap.workspace.database.db2.DB2Installer;
import jp.co.worksap.workspace.ide.eclipse.EclipseConfiguration;
import jp.co.worksap.workspace.ide.eclipse.EclipseInstaller;
import jp.co.worksap.workspace.ide.eclipse.EclipsePlugin;
import jp.co.worksap.workspace.ide.eclipse.EclipsePluginInstaller;
import jp.co.worksap.workspace.ide.eclipse.Version;
import jp.co.worksap.workspace.lombok.LombokConfiguration;
import jp.co.worksap.workspace.lombok.LombokInstaller;
import jp.co.worksap.workspace.packagemanagement.Package;
import jp.co.worksap.workspace.packagemanagement.PackageManagementFacade;
import jp.co.worksap.workspace.repository.git.GitInitializer;
import jp.co.worksap.workspace.wasinstall.WASInstaller;
import jp.co.worksap.workspace.wasprofile.WebSphereProfileCreator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class ProvisionerTest {
    @Mock
    private PackageManagementFacade packageManagerFacade;
    @Mock
    private EclipseInstaller eclipseInstaller;
    @Mock
    private EclipsePluginInstaller eclipsePluginInstaller;
    @Mock
    private LombokInstaller lombokInstaller;
    @Mock
    private DB2Installer db2Installer;
    @Mock
    private WASInstaller wasInstaller;
    @Mock
    private WebSphereProfileCreator wasProfile;
    @Mock
    private GitInitializer gitInitializer;

    private Configuration configuration;

    @Before
    public void buildConfiguration() {
        configuration = new Configuration();
    }

    @Test
    public void callPackageInstallWhenPackageIsNotEmpty() throws IOException {
        List<Package> packageList = Lists.newArrayList(Package.of("git"));
        configuration.setTargetPackage(packageList);
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(packageManagerFacade, only()).install(Matchers.<Iterable<Package>> any());
    }

    @Test
    public void skipPackageInstallWhenPackageIsEmpty() throws IOException {
        List<Package> emptyList = Lists.newArrayList();
        configuration.setTargetPackage(emptyList);
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(packageManagerFacade, never()).install(Matchers.<Iterable<Package>> any());
    }

    @Test
    public void callEclipseInstallWhenEclipseConfigIsNonnull() throws IOException {
        configuration.setEclipse(new EclipseConfiguration(Version.fromString("juno"), "UTF-8", null, null, null, null));
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(eclipseInstaller, only()).install(any(EclipseConfiguration.class), any(File.class));
    }

    @Test
    public void skipEclipseInstallWhenEclipseConfigIsNull() throws IOException {
        configuration.setEclipse(null);
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(eclipseInstaller, never()).install(any(EclipseConfiguration.class), any(File.class));
    }

    @Test
    public void callEclipsePluginInstallWhenEclipseConfigIsNonnull() throws IOException {
        List<EclipsePlugin> pluginList = Lists.newArrayList(EclipsePlugin.of("egit"));
        configuration.setEclipse(new EclipseConfiguration(Version.fromString("juno"), "UTF-8", pluginList, null, null, null));
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(eclipsePluginInstaller, only()).install(any(EclipseConfiguration.class), any(File.class));
    }

    @Test
    public void skipEclipsePlugubInstallWhenEclipseConfigIsNull() throws IOException {
        configuration.setEclipse(null);
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(eclipsePluginInstaller, never()).install(any(EclipseConfiguration.class), any(File.class));
    }

    @Test
    public void callLombokInstallWhenEclipseConfigAndLombokConfigAreNonnull() throws IOException {
        List<EclipsePlugin> pluginList = Lists.newArrayList(EclipsePlugin.of("egit"));
        configuration.setEclipse(new EclipseConfiguration(Version.fromString("juno"), "UTF-8", pluginList, null, null, null));
        configuration.setLombok(LombokConfiguration.fromString("1.12.2"));
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(lombokInstaller, only()).install(Matchers.<Optional<LombokConfiguration>> any(), any(File.class));
    }

    @Test
    public void skipLombokInstallWhenEclipseConfigIsNull() throws IOException {
        configuration.setEclipse(null);
        // even though lombok config exists, we need eclipse config
        configuration.setLombok(LombokConfiguration.fromString("1.12.2"));
        assertThat(new Provisioner(packageManagerFacade, eclipseInstaller, eclipsePluginInstaller, lombokInstaller, db2Installer, wasInstaller, wasProfile, gitInitializer).execute(configuration), is(StatusCode.NORMAL));
        verify(lombokInstaller, never()).install(Matchers.<Optional<LombokConfiguration>> any(), any(File.class));
    }
}
