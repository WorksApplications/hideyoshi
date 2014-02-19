package jp.co.worksap.workspace.ide.eclipse;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.OperatingSystem;
import jp.co.worksap.workspace.common.OperatingSystemKeyDeserializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.google.common.base.Charsets;


@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class EclipseConfiguration {
    private Version version;
    private String defaultCharset;
    private List<EclipsePlugin> plugin;
    private List<String> pluginRepository;
    @JsonDeserialize(keyUsing = OperatingSystemKeyDeserializer.class)
    private Map<OperatingSystem, String> downloadFrom;

    @Nonnull
    public Charset getDefaultCharset() {
        if (defaultCharset == null) {
            return Charsets.UTF_8;
        } else {
            return Charset.forName(defaultCharset);
        }
    }

    @Nonnull
    public List<EclipsePlugin> getPlugin() {
        if (plugin == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(plugin);
        }
    }

    @Nonnull
    public List<String> getPluginRepository() {
        if (pluginRepository == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(pluginRepository);
        }
    }

    @Nonnull
    public Map<OperatingSystem, String> getDownloadFrom() {
        if (downloadFrom == null) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(downloadFrom);
        }
    }
}
