package jp.co.worksap.workspace.cli;

import java.io.File;
import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

import org.kohsuke.args4j.Option;

import com.google.common.base.Optional;

public class CliOption {
    private static final String DEFAULT_CONFIG_FILE = "./project.json";

    @Getter
    @Setter
    @Option(name = "-h", aliases = "--help", usage = "show help")
    private boolean helpRequired;

    @Getter
    @Setter
    @Option(name = "-d", aliases = "--debug", usage = "output debug logs")
    private boolean debug;

    @Nullable
    @Setter
    @Option(name = "-l", aliases = "--log", metaVar = "debug.log", usage = "file to log")
    private File logFile = null;

    @Getter
    @Setter
    @Nullable
    @Option(name = "-f", aliases = "--file", metaVar = "config.json", usage = "specify configuration file")
    private URI configurationFile = URI.create(DEFAULT_CONFIG_FILE);

    @Getter
    @Setter
    @Nullable
    @Option(name = "-t", aliases = "--target-location", metaVar = "path/to/workspace", usage = "specify path of directory to set up workspace")
    private File targetLocation;

    @Nonnull
    public Optional<File> getLogFile() {
        return Optional.fromNullable(logFile);
    }
}
