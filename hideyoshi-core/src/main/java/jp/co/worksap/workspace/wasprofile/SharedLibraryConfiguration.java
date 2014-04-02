package jp.co.worksap.workspace.wasprofile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SharedLibraryConfiguration {
    private String libName;
    private String libClassPath;
    private String clMode;
}
