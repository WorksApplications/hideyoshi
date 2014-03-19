package jp.co.worksap.workspace.wasinstall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.worksap.workspace.common.DownloadFile;
import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.PipingDaemon;
import jp.co.worksap.workspace.common.UnArchiver;
import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Files;
import com.google.common.io.Resources;

@Slf4j
public class WASInstaller {
    public void install(WASInstallConfiguration configuration) {
        // TODO check if already exists and if yes than do I install update
        // packages?
        try {
            URL downloadUrl = configuration.getUrlToDownload();
            File downloadedFile = File.createTempFile("was", ".download");
            DownloadFile downloader = new DownloadFile();

            // downloading and installing WAS
            if (wasExists(configuration)) {
                log.info("WAS already exists at install location.");
            } else {
                downloader.download(downloadUrl, downloadedFile);
                unpack(downloadedFile);
                setup(configuration, downloadedFile.getParentFile(), "WAS");
            }
            if (!configuration.getUpdatePackages().isEmpty()) {

                // downloading and installing UpdateInstaller
                if (wasUpdaterExists(configuration)) {
                    log.info("WAS update installer already exists.");
                } else {
                    downloadUrl = configuration.getUpdateInstallerUrlToDownload();
                    downloader.download(downloadUrl, downloadedFile);
                    unpack(downloadedFile);
                    setup(configuration, downloadedFile.getParentFile(), "UpdateInstaller");
                }

                StringBuilder packages = new StringBuilder();

                // downloading and installing update packages
                for (UpdatePackage updatePack : updatePackageToInstall(configuration)) {
                    downloadUrl = updatePack.getUrlToDownload();
                    downloadedFile = File.createTempFile("was", ".pak");
                    downloader.download(downloadUrl, downloadedFile);
                    packages.append(downloadedFile.getAbsolutePath() + ";");
                }
                update(configuration, packages.toString());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void unpack(File downloadedFile) {
        try {
            log.info("Starting unpacking...");
            new UnArchiver().extract(downloadedFile, downloadedFile.getParentFile());
        } finally {
            log.info("Unpacking completed...");
        }
    }

    private void setup(WASInstallConfiguration configuration, File location, String product) {
        try {
            OperatingSystem os = OperatingSystem.create();
            String setupPath = location.getAbsolutePath() + "/" + product + "/" + os.appendExtensionTo("install");
            File rspFile = File.createTempFile(product, ".rsp");
            Resources.copy(WASInstaller.class.getResource(product + ".rsp"), Files.asByteSink(rspFile).openStream());

            FileWriter rspFileWriter = new FileWriter(rspFile, true);
            rspFileWriter.write("\n-OPT installLocation=\"" + configuration.getInstallLocation() + "\\");
            if (product.equals("WAS")) {
                rspFileWriter.write("AppServer\\\"");
            } else {
                rspFileWriter.write(product + "\"");
            }
            rspFileWriter.flush();

            ProcessBuilder builder = new ProcessBuilder(setupPath, "-options", rspFile.getAbsolutePath(), "-silent");
            log.info("execute command ({}) to install " + product, builder.command());
            Process process = builder.start();
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                int statusCode = process.waitFor();
                if (statusCode != 0) {
                    throw new IllegalArgumentException("Failed to install " + product + ", status code is " + statusCode);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
                rspFileWriter.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void update(WASInstallConfiguration configuration, String packages) {
        try {
            OperatingSystem os = OperatingSystem.create();
            String setupPath = "\"" + configuration.getInstallLocation() + "\\UpdateInstaller\\" + os.appendExtensionTo("update") + "\"";
            File rspFile = File.createTempFile("WAS.update", ".rsp");

            FileWriter rspFileWriter = new FileWriter(rspFile, true);
            rspFileWriter.write("\n-W product.location=\"" + configuration.getInstallLocation() + "\\AppServer\\\"");
            rspFileWriter.write("\n-W maintenance.package=\"" + packages + "\"");
            rspFileWriter.write("\n-W update.type=\"install\"");
            rspFileWriter.flush();

            ProcessBuilder builder = new ProcessBuilder(setupPath, "-options", rspFile.getAbsolutePath(), "-silent");
            log.info("execute command ({}) to install update packages", builder.command());
            Process process = builder.start();
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                int statusCode = process.waitFor();
                if (statusCode != 0) {
                    throw new IllegalArgumentException("Failed to install update packages, status code is " + statusCode);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
                rspFileWriter.close();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Websphere Install", "stdout");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Websphere Install", "stderr");
        daemon.start();
    }

    private boolean wasExists(WASInstallConfiguration configuration) {
        return new File(configuration.getInstallLocation(), "AppServer").exists();
    }

    private boolean wasUpdaterExists(WASInstallConfiguration configuration) {
        return new File(configuration.getInstallLocation(), "UpdateInstaller").exists();
    }

    private List<UpdatePackage> updatePackageToInstall(WASInstallConfiguration configuration) {
        List<UpdatePackage> packages = new ArrayList<UpdatePackage>();
        List<String> installedPackages = new ArrayList<String>();
        try {
            String setupPath = "\"" + configuration.getInstallLocation() + "\\UpdateInstaller\\bin\\installRegistryUtils.bat\"";
            ProcessBuilder builder = new ProcessBuilder(setupPath, "-listPackages");
            log.info("execute command ({}) to list update packages", builder.command());
            Process process = builder.start();
            try {
                process.getOutputStream().close();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    line = line.toLowerCase();
                    if (line.contains("package name")){
                        installedPackages.add(line.replace("package name", "").trim());
                    }
                }
                int statusCode = process.waitFor();
                if (statusCode != 0) {
                    throw new IllegalArgumentException("Failed to list packages, status code is " + statusCode);
                }
            } catch (InterruptedException | IOException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (UpdatePackage updatePackage : configuration.getUpdatePackages()) {
            if (!installedPackages.contains(updatePackage.getName().toLowerCase())) {
                packages.add(updatePackage);
            }
        }
        return packages;
    }
}
