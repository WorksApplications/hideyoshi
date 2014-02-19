package jp.co.worksap.workspace.cli;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;


import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

class ConfigurationLoader {
    /**
     * <p>Load configuration from local file. Configuration file can be formatted
     * in strict JSON or HOCON(Human-Optimized Config Object Notation).</p>
     * @param configurationFile configuration file which is written in JSON format or HOCON format
     * @return deserialized configuration instance
     * @see https://github.com/typesafehub/config/blob/master/HOCON.md
     */
    Configuration loadFrom(@Nonnull File configurationFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String reconstructedJson = reconstruct(configurationFile);
            return mapper.readValue(reconstructedJson, Configuration.class);
        } catch (JsonParseException | JsonMappingException e) {
            throw new IllegalArgumentException(
                    "configuration file has illegal format", e);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "IOException while loading configuration", e);
        }
    }

    /**
     * <p>Convert HOCON format to JSON format.</p>
     * @param hoconFile configuration file which is written in HOCON format
     * @return configuration which is written in JSON format
     * @throws IOException
     */
    private String reconstruct(File hoconFile) throws IOException {
        Config parsed = ConfigFactory.parseFile(hoconFile).resolve();
        return parsed.root().render(ConfigRenderOptions.concise());
    }
}
