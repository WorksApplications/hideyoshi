package jp.co.worksap.workspace.wasprofile;

import lombok.Getter;

@Getter
public class DataSourcesConfiguration {
    private String name;
    private String jndiName;    
    private String componentManagedAuthenticationAlias;
    private String xaRecoveryAuthAlias; 
}
