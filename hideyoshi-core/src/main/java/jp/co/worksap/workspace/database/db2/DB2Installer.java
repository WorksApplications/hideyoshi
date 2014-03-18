package jp.co.worksap.workspace.database.db2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.CheckReturnValue;

import jp.co.worksap.workspace.common.DownloadFile;
import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.PathWalker;
import jp.co.worksap.workspace.common.PipingDaemon;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;

@Slf4j
public class DB2Installer {
    private static final Joiner COMMAND_JOINER = Joiner.on(' ');
    private final File db2cmd;

    public DB2Installer() throws IOException {
        OperatingSystem os = OperatingSystem.create();
        PathWalker pathWalker = new PathWalker(new PathWalker.PathFindStrategy());
        Optional<File> command = pathWalker.findOnPath(os.appendExtensionTo("db2cmd"));
        if (command.isPresent()) {
            db2cmd = command.get();
        } else {
            // no command found, we use default path
            db2cmd = new File("C:/Program Files/IBM/SQLLIB/BIN/db2cmd.exe");
            assert !db2cmd.exists();
        }
    }

    public void install(DB2Configuration configuration) {
        try {
            if (!db2cmd.exists()) {
                URL downloadUrl = configuration.getUrlToDownload();
                File downloadedFile = File.createTempFile("db2", ".download");
                new DownloadFile().download(downloadUrl, downloadedFile);
                unpack(downloadedFile);
                setup(configuration, downloadedFile.getParentFile());
            }
            configure(configuration);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void unpack(File downloadedFile) {
        try {
            log.info("Unpacking installer...");
            ZipFile zipped = new ZipFile(downloadedFile);
            zipped.extractAll(downloadedFile.getParent());
        } catch (ZipException e) {
            throw new IllegalStateException(e);
        } finally {
            log.info("Unpacking completed...");
        }
    }

    private void setup(DB2Configuration configuration, File location) {
        try {
            // TODO OS Dependent
            String setupPath = location.getAbsolutePath() + "/WSER/image/setup.exe";
            File rspFile = File.createTempFile("db2Config", ".rsp");
            Resources.copy(DB2Installer.class.getResource("db2.rsp"), Files.asByteSink(rspFile).openStream());

            FileWriter rspFileWriter = new FileWriter(rspFile, true);
            rspFileWriter.write("\nDB2.USERNAME=" + configuration.getUsername());
            rspFileWriter.write("\nDB2.PASSWORD=" + configuration.getPassword());
            rspFileWriter.write("\nDAS_USERNAME=" + configuration.getUsername());
            rspFileWriter.write("\nDAS_PASSWORD=" + configuration.getPassword());
            rspFileWriter.flush();
            ProcessBuilder builder = new ProcessBuilder(setupPath, "/u", rspFile.getAbsolutePath());
            log.info("execute command ({}) to install db2", builder.command());
            Process process = builder.start();
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                int statusCode = process.waitFor();
                if (statusCode != 0) {
                    throw new IllegalArgumentException("Failed to install db2, status code is " + statusCode);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                rspFileWriter.close();
                process.destroy();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private void configure(DB2Configuration configuration) {
        catalogNodes(configuration.getNodes());
        catalogDatabases(configuration.getDatabases());
    }

    private void catalogDatabases(List<Database> databases) {
        boolean error = false;
        StringBuilder errorMessage = new StringBuilder("Failed to catalog database ");
        for (Database database : databases) {
            if (databaseExists(database)) {
                Process process = buildProcess("UNCATALOG DATABASE " + database.getAlias());
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    int statusCode = process.waitFor();
                    if (statusCode != 0) {
                        error = true;
                        errorMessage.append(database.getAlias() + ", ");
                    }
                } catch (InterruptedException | IOException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }
            }
            Process process = buildProcess(database.catalogCommand());
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                int statusCode = process.waitFor();
                if (statusCode != 0) {
                    error = true;
                    errorMessage.append(database.getAlias() + ", ");
                }
            } catch (InterruptedException | IOException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
                log.info(database.getAlias() + " : Done!!");
            }

        }
        if (error) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    private void catalogNodes(List<Node> nodes) {
        boolean error = false;
        StringBuilder errorMessage = new StringBuilder("Failed to catalog node ");
        for (Node node : nodes) {
            if (nodeExists(node)) {
                Process process = buildProcess("UNCATALOG NODE " + node.getNode());
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    int statusCode = process.waitFor();
                    if (statusCode != 0) {
                        error = true;
                        errorMessage.append(node.getNode() + ", ");
                    }
                } catch (InterruptedException | IOException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }
            }
            Process process = buildProcess(node.catalogCommand());
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                int statusCode = process.waitFor();
                if (statusCode != 0) {
                    error = true;
                    errorMessage.append(node.getNode() + ", ");
                }
            } catch (InterruptedException | IOException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
                log.info(node.getNode() + " : Done!!");
            }
        }
        if (error) {
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    @CheckReturnValue
    @VisibleForTesting
    boolean nodeExists(Node node) {
        Process process = buildProcess("LIST NODE DIRECTORY");
        try {
            process.getOutputStream().close();
            // TODO choose correct charset dynamically
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("SHIFT-JIS")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.toLowerCase();
                if ((line.contains("node name") || line.contains("ノード名")) && line.contains(node.getNode().toLowerCase())) {
                    return true;
                }
            }
            int statusCode = process.waitFor();
            if (statusCode != 0) {
                return false;
            }
        } catch (InterruptedException | IOException e) {
            throw new IllegalStateException(e);
        } finally {
            process.destroy();
        }
        return false;
    }

    @CheckReturnValue
    @VisibleForTesting
    boolean databaseExists(Database database) {
        Process process = buildProcess("LIST DATABASE DIRECTORY");
        try {
            process.getOutputStream().close();
            // TODO choose correct charset dynamically
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("SHIFT-JIS")));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.toLowerCase();
                if ((line.contains("database alias") || line.contains("データベース別名")) && line.contains(database.getAlias().toLowerCase())) {
                    return true;
                }
            }
            int statusCode = process.waitFor();
            if (statusCode != 0) {
                return false;
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            process.destroy();
        }
        return false;
    }

    private Process buildProcess(String process) {
        try {
            List<String> command = Lists.newArrayList(db2cmd.getAbsolutePath(), "/c", "/w", "/i", "db2", process);
            ProcessBuilder builder = new ProcessBuilder(command);
            log.info("execute command ({}) to configure db2", COMMAND_JOINER.join(command));
            return builder.start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "DB2 Install", "stdout");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "DB2 Install", "stderr");
        daemon.start();
    }
}
