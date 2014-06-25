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
public class JDBCProviderConfiguration {
    private String scope;
    private String databaseType;
    private String providerType;
    private String implementationType;
    private String name;
    private String description;
    private String implementationClassName;
    private String classpath;
    private String nativePath;
    private String isolated;
}
