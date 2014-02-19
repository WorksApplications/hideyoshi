package jp.co.worksap.workspace.ide.eclipse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class VersionTest {

    @Test
    public void testFromString() {
        assertThat(Version.fromString("1.0.0"), is(new Version(1, 0, 0)));
        assertThat(Version.fromString("kepler"), is(new Version(4, 3, 1)));
    }

    @Test
    public void deserialize() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Version version = mapper.readValue("\"1.2.3\"", Version.class);
        assertThat(version, is(equalTo(Version.fromString("1.2.3"))));
    }
}
