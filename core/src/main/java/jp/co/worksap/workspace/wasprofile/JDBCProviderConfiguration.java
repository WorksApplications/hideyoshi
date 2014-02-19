package jp.co.worksap.workspace.wasprofile;

import lombok.Getter;

@Getter
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
