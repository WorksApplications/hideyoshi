package jp.co.worksap.workspace.lombok;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class LombokTest {
    @Test
    public void getDownloadUrl() {
        assertThat(
                LombokConfiguration.fromString("1.12.2").getUrlToDownload().toString(),
                is("https://projectlombok.googlecode.com/files/lombok-1.12.2.jar"));
    }

    @Test
    public void deserialize() throws IOException {
        LombokConfiguration config = new ObjectMapper().readValue("{\"version\": \"1.12.2\"}", LombokConfiguration.class);

        assertThat(config, is(LombokConfiguration.fromString("1.12.2")));
    }
}
