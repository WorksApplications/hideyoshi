package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PluginAliasConverterTest {

    @Test
    public void test() {
        assertThat(new PluginAliasConverter().apply("egit"), is("org.eclipse.egit.feature.group"));
    }

}
