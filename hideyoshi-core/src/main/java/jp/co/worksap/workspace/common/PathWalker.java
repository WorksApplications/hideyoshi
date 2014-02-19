package jp.co.worksap.workspace.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;

/**
 * <p>Walk through all directory in PATH, and find file which has specified name.</p>
 * @author Kengo TODA (toda_k@worksap.co.jp)
 */
public final class PathWalker {
    @Nonnull
    private final Iterable<String> path;

    public PathWalker(@Nonnull PathFindStrategy strategy) {
        this.path = checkNotNull(strategy).findPath();
    }

    /**
     * <p>Find a file which is </p>
     * @param filename
     * @return
     * @throws IOException
     */
    @Nonnull
    public Optional<File> findOnPath(@Nonnull String filename) throws IOException {
        checkNotNull(filename);

        for (String directory : path) {
            File expectedFile = new File(directory, filename);
            if (expectedFile.exists() && expectedFile.isFile()) {
                return Optional.of(expectedFile);
            }
        }

        return Optional.absent();
    }

    /**
     * <p>Strategy to make {@link PathWalker} testable.</p>
     * @author Kengo TODA (toda_k@worksap.co.jp)
     */
    public static class PathFindStrategy {
        public Iterable<String> findPath() {
            return Splitter.on(File.pathSeparator).split(System.getenv("PATH"));
        }
    }
}
