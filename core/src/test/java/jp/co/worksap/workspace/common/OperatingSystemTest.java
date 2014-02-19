package jp.co.worksap.workspace.common;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jp.co.worksap.workspace.common.OperatingSystem;

import org.junit.Test;

public class OperatingSystemTest {

    @Test
    public void fromString() {
        assertThat(OperatingSystem.fromString("win.64"), is(OperatingSystem.WIN64));
        assertThat(OperatingSystem.fromString("osx.32"), is(OperatingSystem.OSX32));
    }

}
