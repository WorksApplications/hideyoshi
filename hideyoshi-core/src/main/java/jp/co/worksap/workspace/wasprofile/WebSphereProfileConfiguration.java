package jp.co.worksap.workspace.wasprofile;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class WebSphereProfileConfiguration {
    private String cellName;
    private String nodeName;
    private String serverName;
    private String platform;
    private String installPath;
    private String profileName;
    private JDBCProviderConfiguration jdbcProvider;
    private JVMHeapSizeConfiguration jvmHeapSize;
    private SharedLibraryConfiguration sharedLibrary;
    private List<DataSourceConfiguration> dataSource;
    private List<GlobalSecurityConfiguration> globalSecurity;
}
