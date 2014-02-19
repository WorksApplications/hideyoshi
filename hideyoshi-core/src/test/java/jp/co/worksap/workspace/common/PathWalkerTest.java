package jp.co.worksap.workspace.common;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import jp.co.worksap.workspace.common.PathWalker.PathFindStrategy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class PathWalkerTest {
    private static final String TARGET_FILE_TO_FIND = "target.exe";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testFind() throws IOException {
        folder.newFile(TARGET_FILE_TO_FIND);
        PathFindStrategy strategy = new PathFindStrategy() {
            @Override
            public Iterable<String> findPath() {
                return Lists.newArrayList(folder.getRoot().getAbsolutePath());
            }
        };

        assertTrue(new PathWalker(strategy).findOnPath(TARGET_FILE_TO_FIND).isPresent());
        assertFalse(new PathWalker(strategy).findOnPath("notexist").isPresent());
    }

    @Test
    public void returnAbsentWhenFileDoesNotExist() throws IOException {
        PathFindStrategy strategy = new PathFindStrategy() {
            @Override
            public Iterable<String> findPath() {
                return Lists.newArrayList(folder.getRoot().getAbsolutePath());
            }
        };

        assertFalse(new PathWalker(strategy).findOnPath(TARGET_FILE_TO_FIND).isPresent());
    }

    @Test
    public void firstFileInPathShouldBeReturned() throws IOException {
        final File firstDir = folder.newFolder();
        final File secondDir = folder.newFolder();

        File fileShouldBeFound = new File(firstDir, TARGET_FILE_TO_FIND);
        Files.touch(fileShouldBeFound);
        Files.touch(new File(secondDir, TARGET_FILE_TO_FIND));

        PathFindStrategy strategy = new PathFindStrategy() {
            @Override
            public Iterable<String> findPath() {
                return Lists.newArrayList(firstDir.getAbsolutePath(), secondDir.getAbsolutePath());
            }
        };

        File found = new PathWalker(strategy).findOnPath(TARGET_FILE_TO_FIND).get();
        assertThat(found, is(equalTo(fileShouldBeFound)));
    }
}
