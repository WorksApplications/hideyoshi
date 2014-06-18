package jp.co.worksap.workspace.cli;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.download.Downloader;
import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.io.Files;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

@Slf4j
class ConfigurationLoader {
    /**
     * <p>Load configuration from local file. Configuration file can be formatted
     * in strict JSON or HOCON(Human-Optimized Config Object Notation).</p>
     * @param configurationFile configuration file which is written in JSON format or HOCON format
     * @param targetLocation which specified by command line argument
     * @return deserialized configuration instance
     * @see https://github.com/typesafehub/config/blob/master/HOCON.md
     */
    Configuration loadFrom(@Nonnull File configurationFile, File targetLocation) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String reconstructedJson = reconstruct(configurationFile);
            Configuration configuration = mapper.readValue(reconstructedJson, Configuration.class);
            if (targetLocation != null) {
                log.info("as targetLocation, we use command line parameter ({}) instead of parameter in configuration file ({})",
                            targetLocation,
                            configuration.getTargetLocation());
                configuration.setTargetLocation(targetLocation);
            }
            return configuration;
        } catch (JsonParseException | JsonMappingException e) {
            throw new IllegalArgumentException(
                    "configuration file has illegal format", e);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "IOException while loading configuration", e);
        }
    }

    Configuration loadFrom(@Nonnull URI configurationUri, File targetLocation) throws IOException {
        configurationUri = changeToAbsolute(configurationUri);
        Downloader downloader = Downloader.createFor(configurationUri, new SystemInAuthenticationInfoProvider());
        File configurationFile = File.createTempFile("configuration", Files.getFileExtension(configurationUri.getPath()));
        downloader.download(configurationUri, configurationFile);
        return loadFrom(configurationFile, targetLocation);
    }

    /**
     * <p>We should ensure that URI is absolute before we call {@code URI#toURL()},
     * this method will convert relative file path to absolute URI.
     */
    @Nonnull
    private URI changeToAbsolute(URI configurationUri) {
        if (configurationUri.isAbsolute()) {
            return configurationUri;
        }

        File absolutePath = new File(".", configurationUri.toString());
        return absolutePath.toURI();
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
