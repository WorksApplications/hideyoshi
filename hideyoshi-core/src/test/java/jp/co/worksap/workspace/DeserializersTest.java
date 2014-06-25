package jp.co.worksap.workspace;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.OperatingSystemKeyDeserializer;
import jp.co.worksap.workspace.ide.eclipse.EclipsePlugin;
import jp.co.worksap.workspace.ide.eclipse.Version;
import lombok.Setter;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.io.Resources;

/**
 * <p>Test case to ensure that each XxxDeserializer works well.
 */
public class DeserializersTest {
    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        Data parsedData = mapper.readValue(Resources.getResource("DeserializersTest.json"), Data.class);
        Map<OperatingSystem, OperatingSystem> expectedMap = Maps.newHashMap();
        expectedMap.put(OperatingSystem.WIN32, OperatingSystem.WIN32);

        assertThat(parsedData.version, is(equalTo(Version.fromString("1.0.0"))));
        assertThat(parsedData.plugin, is(equalTo(EclipsePlugin.of("org.eclipse.egit.feature.group"))));
        assertThat(parsedData.os, is(equalTo(expectedMap)));
    }

    @Setter
    public static final class Data {
        Version version;
        @JsonDeserialize(keyUsing = OperatingSystemKeyDeserializer.class)
        Map<OperatingSystem, OperatingSystem> os;
        EclipsePlugin plugin;
    }
}
