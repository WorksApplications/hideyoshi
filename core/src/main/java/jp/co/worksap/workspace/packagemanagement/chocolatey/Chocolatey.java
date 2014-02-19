package jp.co.worksap.workspace.packagemanagement.chocolatey;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.PathWalker;
import jp.co.worksap.workspace.common.PathWalker.PathFindStrategy;
import jp.co.worksap.workspace.common.PipingDaemon;
import jp.co.worksap.workspace.packagemanagement.FailToInstallException;
import jp.co.worksap.workspace.packagemanagement.Package;
import jp.co.worksap.workspace.packagemanagement.PackageManager;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * <p>Core class for package management of Windows.</p>
 * @author Kengo TODA (toda_k@worksap.co.jp)
 */
@Slf4j public class Chocolatey implements PackageManager {
    private static final Joiner COMMAND_JOINER = Joiner.on(' ');
    private PackageManager nextManager;
    private PathWalker pathWalker;

    public Chocolatey(@Nonnull PackageManager nextManager) throws IOException {
        this(nextManager, new PathFindStrategy());
    }

    Chocolatey(@Nonnull PackageManager nextManager, @Nonnull PathFindStrategy pathFinder) throws IOException {
        this.nextManager = checkNotNull(nextManager);
        this.pathWalker = new PathWalker(pathFinder);
    }

    @Override
    public void install(@Nonnull Package packageToBeInstalled) throws IOException {
        checkNotNull(packageToBeInstalled);
        Optional<File> binary = findInstalledBinary();
        if (!binary.isPresent()) {
            throw new IllegalStateException("Chocolatey does not exist, please install it first.");
        }

        String packageName = packageToBeInstalled.getName();
        Optional<String> version = packageToBeInstalled.getVersion();
        log.debug("Installing {}:{}", packageName, version.or("latest"));

        Process process = startProcess(binary, packageName, version);
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();

            final int statusCode = process.waitFor();
            log.debug("command returned status code: {}", statusCode);

            if (statusCode != 0) {
                log.warn("Fail to install {}:{} (status code is {}), try to other package manager",
                        packageName,
                        version.or("latest"),
                        statusCode);
                nextManager.install(packageToBeInstalled);
            }
        } catch (InterruptedException e) {
            throw new FailToInstallException("Chocolatey process has been interrupted", e);
        } finally {
            process.destroy();
        }
    }

    Optional<File> findInstalledBinary() throws IOException {
        return pathWalker.findOnPath("choco.bat");
    }

    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Chocolatey", "stdout");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Chocolatey", "stderr");
        daemon.start();
    }

    private Process startProcess(Optional<File> binary, String packageName,
            Optional<String> version) throws IOException {
        List<String> command = Lists.newArrayList("cmd.exe", "/C", "CALL", binary.get().getAbsolutePath(), "install", packageName);
        if (version.isPresent()) {
            command.add("-version");
            command.add(version.get());
        }

        log.debug("Following command will be executed: {}", COMMAND_JOINER.join(command));
        ProcessBuilder builder = new ProcessBuilder(command);

        return builder.start();
    }
}
