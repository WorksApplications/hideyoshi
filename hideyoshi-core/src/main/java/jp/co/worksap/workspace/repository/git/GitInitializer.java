package jp.co.worksap.workspace.repository.git;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.google.common.annotations.VisibleForTesting;

@Slf4j
@ParametersAreNonnullByDefault
public class GitInitializer {
    public void initialize(File targetLocation, Map<String, GitRepositoryConfiguration> repositories, Map<String, GitHookConfiguration> hooks) {
        for (Map.Entry<String, GitRepositoryConfiguration> repository : repositories.entrySet()) {
            GitHookConfiguration hook = findHookFor(repository.getValue(), hooks);
            String repositoryName = repository.getKey();
            File directory = new File(targetLocation, repositoryName);
            if (!directory.mkdir()) {
                throw new IllegalArgumentException("Cannot create directory. Verify that repository name is valid as file name:" + repositoryName);
            }

            clone(repository.getValue(), directory);
            if (hook != null) {
                initHook(directory, hook);
            }
        }
    }

    @VisibleForTesting
    void clone(GitRemoteHost remoteHost, File directory) {
        log.info("Cloning remote Git repository to {}...", directory.getAbsolutePath());

        CloneCommand clone = new CloneCommand();
        clone.setBranch(remoteHost.getBranch()).setDirectory(directory).setURI(remoteHost.getUri());
        try {
            // TODO support passphrase
            clone.call();
        } catch (GitAPIException e) {
            throw new IllegalStateException("Fail to clone remote Git repository.", e);
        }
    }

    @VisibleForTesting
    void initHook(File directory, GitHookConfiguration hook) {
        File gitDir = new File(directory, ".git");
        File gitHookDir = new File(gitDir, "hooks");
        try {
            FileUtils.deleteDirectory(gitHookDir);
        } catch (IOException e) {
            throw new IllegalStateException("Fail to remove default hook scripts.", e);
        }
        clone(hook, gitHookDir);
    }

    @Nullable
    @CheckForNull
    private GitHookConfiguration findHookFor(GitRepositoryConfiguration value, @Nullable Map<String, GitHookConfiguration> hooks) {
        String hookName = value.getHook();
        if (hookName == null || hooks == null) {
            return null;
        } else {
            return hooks.get(hookName);
        }
    }
}
