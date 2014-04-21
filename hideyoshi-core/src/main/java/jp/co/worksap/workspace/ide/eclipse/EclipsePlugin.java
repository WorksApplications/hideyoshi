package jp.co.worksap.workspace.ide.eclipse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Value;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@Value
@JsonDeserialize(using = EclipsePluginDeserializer.class)
public class EclipsePlugin {
    @Nonnull
    private String id;
    @Nullable
    private String version;

    /**
     * <p>Build command line parameter from this instance</p>
     * @see http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Fp2_director.html
     */
    public String toString() {
        if (version == null) {
            return id;
        } else {
            return id + '/' + version;
        }
    }

    public static EclipsePlugin of(@Nonnull String pluginId) {
        return of(pluginId, null);
    }

    public static EclipsePlugin of(@Nonnull String pluginId, @Nullable String version) {
        return new EclipsePlugin(pluginId, version);
    }
}
