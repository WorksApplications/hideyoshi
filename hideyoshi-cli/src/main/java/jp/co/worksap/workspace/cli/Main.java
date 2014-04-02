package jp.co.worksap.workspace.cli;

import java.io.IOException;

import jp.co.worksap.workspace.database.db2.DB2Installer;
import jp.co.worksap.workspace.ide.eclipse.EclipseInstaller;
import jp.co.worksap.workspace.ide.eclipse.EclipsePluginInstaller;
import jp.co.worksap.workspace.lombok.LombokInstaller;
import jp.co.worksap.workspace.packagemanagement.PackageManagementFacade;
import jp.co.worksap.workspace.repository.git.GitInitializer;
import jp.co.worksap.workspace.wasinstall.WASInstaller;
import jp.co.worksap.workspace.wasprofile.WebSphereProfileCreator;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
    public static void main(String[] args) throws IOException {
        StatusCode status = new Main().execute(args);
        int statusCode = status.getCode();
        System.exit(statusCode);
    }

    StatusCode execute(String[] args) throws IOException {
        CliOption bean = new CliOption();
        CmdLineParser parser = new CmdLineParser(bean);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            // Logger is not initialized yet, so we use SYSERR instead.
            System.err.println("Illegal command line argument");
            e.printStackTrace();
            return StatusCode.ERROR;
        }

        if (bean.isHelpRequired()) {
            parser.printUsage(System.err);
            return StatusCode.NORMAL;
        }

        new LogConfigurator().configureLogger(bean);
        Configuration configuration = new ConfigurationLoader().loadFrom(bean.getConfigurationFile());

        return new Provisioner(new PackageManagementFacade(), new EclipseInstaller(), new EclipsePluginInstaller(), new LombokInstaller(), new DB2Installer(), new WASInstaller(), new WebSphereProfileCreator(), new GitInitializer()).execute(configuration);
    }
}
