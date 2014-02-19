package jp.co.worksap.workspace.database.db2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.co.worksap.workspace.common.OperatingSystem;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class DB2InstallerTest {
    private static final String PASSWORD = "password";
    private static final String USER_NAME = "username";
    private static final String NODE_NAME = "nodename";
    private File installer;

    @Before
    public void ensureWindows64bit() {
        assumeThat(OperatingSystem.create(), is(equalTo(OperatingSystem.WIN64)));
    }

    @Before
    public void ensureWeHaveInstaller() {
        installer = new File("src/test/resources", "DB2_97_limited_CD_Win_x86-64.exe");
        assumeThat("put DB2_97_limited_CD_Win_x86-64.exe to src/test/resources", installer.exists(), is(true));
    }

    @Test
    public void installDB2() throws IOException {
        Node node = new Node(NODE_NAME, "unit-test", "50000", USER_NAME, "node_for_test", "linuxx8664");    // TODO "linuxx8664" should be another literal for Windows 64bit
        List<Node> nodes = ImmutableList.of(node);
        Database database1 = new Database("test1", "alias1", NODE_NAME);
        Database database2 = new Database("test2", "alias2", NODE_NAME);
        Database database3 = new Database("test3", "alias3", NODE_NAME);
        List<Database> databases = ImmutableList.of(database1, database2);
        DB2Configuration db2Configuration = new DB2Configuration(USER_NAME, PASSWORD, installer.toURI().toString(), nodes, databases);
        DB2Installer db2Installer = new DB2Installer();
        db2Installer.install(db2Configuration);

        // TODO assert that db2cmd.exe is in PATH
        assertTrue(db2Installer.nodeExists(node));
        assertTrue(db2Installer.databaseExists(database1));
        assertTrue(db2Installer.databaseExists(database2));
        assertFalse(db2Installer.databaseExists(database3));
    }
}
