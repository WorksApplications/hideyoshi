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
public class DataSourceConfiguration {
    private String databaseName;
    private String driverType;
    private String portNumber;
    private String dataStoreHelperClassName;
    private String name;
    private String jndiName;
    private String componentManagedAuthenticationAlias;
    private String xaRecoveryAuthAlias; 
}
