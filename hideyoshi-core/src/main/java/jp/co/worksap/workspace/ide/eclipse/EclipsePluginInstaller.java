package jp.co.worksap.workspace.ide.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.PipingDaemon;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Joiner;

@Slf4j
public class EclipsePluginInstaller {
    private static final Joiner COMMA_JOINER = Joiner.on(',');

    public void install(EclipseConfiguration configuration, File eclipseDir) {
        try {
            installPlugin(configuration.getPlugin(), configuration.getPluginRepository(), eclipseDir);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }

        File workspace = new File(".");
        new EclipseWorkspaceInitializer().initialize(configuration, workspace);
    }

    private void installPlugin(@Nonnull List<EclipsePlugin> plugins, @Nonnull List<String> pluginRepository, File eclipseDir) throws IOException, InterruptedException {
        if (plugins.isEmpty()) {
            return;
        }
        for (EclipsePlugin plugin : plugins) {
            if (plugin.getVersion() == null) {
                log.warn("version of {} isn't specified", plugin.getId());
            }
        }

        Process process = buildProcess(plugins, pluginRepository, eclipseDir);
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            int status = process.waitFor();
            if (status != 0) {
                throw new IllegalArgumentException("fail to install Eclipse plugin. Status code is " + status);
            }
        } finally {
            process.destroy();
        }
    }

    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Eclipse plugin", "stdout");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Eclipse plugin", "stderr");
        daemon.start();
    }

    private Process buildProcess(List<EclipsePlugin> plugin,
            List<String> pluginRepository, File location) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        File installedDir = new File(location, "eclipse");
        File executable = new File(installedDir, OperatingSystem.create().appendExtensionTo("eclipsec"));
        if (!executable.exists() || !executable.canExecute()) {
            throw new IllegalStateException("Cannot find eclipse at " + executable.getAbsolutePath());
        }

        builder.directory(installedDir);
        builder.command(executable.getAbsolutePath(),
                "-application", "org.eclipse.equinox.p2.director",
                "-installIUs", COMMA_JOINER.join(plugin),
                "-repository", COMMA_JOINER.join(pluginRepository),
                "-destination", installedDir.getAbsolutePath(),
                "-nosplash", "-consolelog");
        log.info("execute command ({})", builder.command());

        Process process = builder.start();
        return process;
    }

}
