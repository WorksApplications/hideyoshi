package jp.co.worksap.workspace.packagemanagement.chocolatey;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import jp.co.worksap.workspace.common.PathWalker.PathFindStrategy;
import jp.co.worksap.workspace.packagemanagement.AlertingPackageManager;

import org.junit.Test;

import com.google.common.collect.Lists;

public class ChocolateyTest {
    @Test
    public void testExistReturnsFalse() throws IOException {
        PathFindStrategy mockedFinder = new PathFindStrategy() {
            @Override
            public Iterable<String> findPath() {
                return Lists.newArrayList();
            }
        };

        assertThat(new Chocolatey(new AlertingPackageManager(), mockedFinder).findInstalledBinary().isPresent(), is(false));
    }
}
