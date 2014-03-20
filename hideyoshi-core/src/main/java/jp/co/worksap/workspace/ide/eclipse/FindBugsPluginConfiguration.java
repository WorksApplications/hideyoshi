package jp.co.worksap.workspace.ide.eclipse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class FindBugsPluginConfiguration {
    /**
     * Path of file which is stored as edu.umd.cs.findbugs.plugin.eclipse.prefs in Eclipse workspace metadata.
     */
    private String metadata;
}
