package jp.co.worksap.workspace.wasprofile;


public class JDBCProviderConfigurator {
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

    public void readConfig(JDBCProviderConfiguration config) {
        scope = config.getScope();
        databaseType = config.getDatabaseType();
        providerType = config.getProviderType();
        implementationType = config.getImplementationType();
        name = config.getName();
        description = config.getDescription();
        implementationClassName = config.getImplementationClassName();
        classpath = config.getClasspath();
        nativePath = config.getNativePath();
        isolated = config.getIsolated();
    }

    public String returnScript(){
        StringBuilder script = new StringBuilder();
        script.append("AdminTask.createJDBCProvider(['-scope', '").append(scope)
                .append("', '-databaseType', '").append(databaseType)
                .append("', '-providerType', '").append(providerType)
                .append("', '-implementationType', '").append(implementationType)
                .append("', '-name', '").append(name)
                .append("', '-description', '").append(description)
                .append("', '-classpath', '").append(classpath)
                .append("', '-nativePath', '").append(nativePath)
                .append("', '-implementationClassName', '").append(implementationClassName)
                .append("', '-isolated', '").append(isolated)
                .append("'])\n");
        script.append("print 'Successfully configured jdbc provider.'\n");
        return script.toString();
    }
}
