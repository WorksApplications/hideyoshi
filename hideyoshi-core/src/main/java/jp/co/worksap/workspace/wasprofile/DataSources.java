package jp.co.worksap.workspace.wasprofile;

import java.util.List;

public class DataSources {
    public String returnScript(WebSphereProfileConfiguration wasConfig) {
        List<DataSourceConfiguration> dataSourceList = wasConfig.getDataSource();
        if (dataSourceList == null) {
            return "";
        }

        StringBuilder script = new StringBuilder();
        for (DataSourceConfiguration dataSource : dataSourceList) {
            String name = dataSource.getName();
            String jndiName = dataSource.getJndiName();
            String databaseName = dataSource.getDatabaseName();
            String driverType = dataSource.getDriverType();
            String serverName = wasConfig.getServerName();
            String portNumber = dataSource.getPortNumber();
            String dataStoreHelperClassName = dataSource.getDataStoreHelperClassName();
            String componentManagedAuthenticationAlias = dataSource.getComponentManagedAuthenticationAlias();
            String xaRecoveryAuthAlias = dataSource.getXaRecoveryAuthAlias();  

            script.append("nodeList = AdminTask.listJDBCProviders().split('\\r\\n')\n");
            script.append("jdbcProvider = nodeList[len(nodeList)-1]\n"); 
            script.append("dsId = \"\"\n");
            script.append("dsList = AdminConfig.getid(\"/DataSource:").append(name).append("\")\n");
            script.append("if (len(dsList) > 0):\n");
            script.append("  for item in dsList.split('\\n'):\n");
            script.append("      item = item.rstrip()\n");
            script.append("      jndiName = AdminConfig.showAttribute(item, \"jndiName\" )\n");
            script.append("      if (\"").append(jndiName).append("\" == jndiName):\n");
            script.append("          dsId = item\n");
            script.append("if (dsId == \"\"):\n");
            script.append("  AdminTask.createDatasource(jdbcProvider , ['-name', '").append(name)
                    .append("', '-jndiName', '").append(jndiName)
                    .append("', '-configureResourceProperties', '[[databaseName java.lang.String ").append(databaseName)
                    .append("] [driverType java.lang.Integer ").append(driverType)
                    .append("] [serverName java.lang.String  ").append(serverName)
                    .append("] [portNumber java.lang.Integer ").append(portNumber)
                    .append("]]', '-dataStoreHelperClassName', '").append(dataStoreHelperClassName)
                    .append("', '-componentManagedAuthenticationAlias', '").append(componentManagedAuthenticationAlias)
                    .append("', '-xaRecoveryAuthAlias', '").append(xaRecoveryAuthAlias)
                    .append("'])\n");
            script.append("else:\n");
            script.append("  print \"").append(name).append(" already exists in this JDBC Provider!\"\n");
            script.append("  print \"Removing existing data source and creating a new one\"\n");
            script.append("  AdminConfig.remove(dsId)\n");
            script.append("  AdminConfig.save()\n");
            script.append("  AdminTask.createDatasource(jdbcProvider , ['-name', '").append(name)
                    .append("', '-jndiName', '").append(jndiName)
                    .append("', '-configureResourceProperties', '[[databaseName java.lang.String ").append(databaseName)
                    .append("] [driverType java.lang.Integer ").append(driverType)
                    .append("] [serverName java.lang.String  ").append(serverName)
                    .append("] [portNumber java.lang.Integer ").append(portNumber)
                    .append("]]', '-dataStoreHelperClassName', '").append(dataStoreHelperClassName)
                    .append("', '-componentManagedAuthenticationAlias', '").append(componentManagedAuthenticationAlias)
                    .append("', '-xaRecoveryAuthAlias', '").append(xaRecoveryAuthAlias)
                    .append("'])\n");
        }
        script.append("print 'Successfully configured data sources.'\n");
        return script.toString();
    }
}
