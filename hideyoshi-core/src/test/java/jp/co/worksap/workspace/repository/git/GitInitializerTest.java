package jp.co.worksap.workspace.repository.git;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import jp.co.worksap.workspace.common.UseSsh;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

public class GitInitializerTest {
    private static final String SSL_REPOSITORY = "git@github.com:WorksApplications/hideyoshi.git";
    private static final String HTTPS_REPOSITORY = "https://github.com/WorksApplications/hideyoshi.git";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCloneHttps() throws IOException {
        testClone(HTTPS_REPOSITORY);
    }

    @Category(UseSsh.class)
    @Test
    public void testCloneSsh() throws IOException {
        testClone(SSL_REPOSITORY);
    }

    @Test
    public void testInitHooks() throws IOException {
        File directory = testClone(HTTPS_REPOSITORY);
        GitHookConfiguration hook = new GitHookConfiguration(
                HTTPS_REPOSITORY,
                "master");
        File gitDir = new File(directory, ".git");
        File gitHooks = new File(gitDir, "hooks");
        assertThat(new File(gitHooks, "README.md").isFile(), is(false));

        GitInitializer initializer = new GitInitializer();
        initializer.initHook(directory, hook);

        assertThat(new File(gitHooks, "README.md").isFile(), is(true));
    }

    private File testClone(String uri) throws IOException {
        File directory = folder.newFolder();
        GitInitializer initializer = new GitInitializer();
        GitRepositoryConfiguration remoteHost = new GitRepositoryConfiguration(
                uri,
                "master",
                null);
        initializer.clone(remoteHost, directory);

        File gitDir = new File(directory, ".git");
        assertThat(gitDir.isDirectory(), is(true));
        return directory;
    }
}
