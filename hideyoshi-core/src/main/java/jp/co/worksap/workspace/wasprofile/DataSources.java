package jp.co.worksap.workspace.wasprofile;

import java.util.ArrayList;

public class DataSources {      
    private String name;
    private String jndiName;
    private String databaseName;
    private String driverType;
    private String serverName;
    private String portNumber;
    private String dataStoreHelperClassName;
    private String componentManagedAuthenticationAlias;
    private String xaRecoveryAuthAlias;
/*
    public void readConfigAndCreateScript(@Nonnull File configFile) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        DataSourcesConfigurationContainer ds = mapper.readValue(configFile,DataSourcesConfigurationContainer.class);
        ArrayList<DataSourcesConfiguration> dataSources = ds.get("dataSources");
        String tmp="";
        for (DataSourcesConfiguration d : dataSources) {
            profilePath = d.getProfilePath();           
            name = d.getName();
            jndiName = d.getJndiName();
            databaseName = d.getDatabaseName();
            driverType = d.getDriverType();
            serverName = d.getServerName();
            portNumber = d.getPortNumber();
            dataStoreHelperClassName = d.getDataStoreHelperClassName();
            componentManagedAuthenticationAlias = d.getComponentManagedAuthenticationAlias();
            xaRecoveryAuthAlias = d.getXaRecoveryAuthAlias();  
            tmp+="nodeList = AdminTask.listJDBCProviders().split('\\r\\n')\n";
            tmp+="jdbcProvider = nodeList[len(nodeList)-1]\n";
            tmp+="AdminTask.createDatasource(jdbcProvider , ['-name', '"+name+"', '-jndiName', '"+jndiName+"', '-configureResourceProperties', '[[databaseName java.lang.String "+databaseName+"] [driverType java.lang.Integer "+driverType+"] [serverName java.lang.String  "+serverName+"] [portNumber java.lang.Integer "+portNumber+"]]', '-dataStoreHelperClassName', '"+dataStoreHelperClassName+"', '-componentManagedAuthenticationAlias', '"+componentManagedAuthenticationAlias+"', '-xaRecoveryAuthAlias', '"+xaRecoveryAuthAlias+"'])\n";
        }

        tmp+="AdminConfig.save()\n";  
        tmp+="print 'Successfully configured data sources.'\n";
        Files.write(tmp, new File(profilePath, "datasources.py"), Charsets.UTF_8);      

        tmp+="AdminConfig.save()\n";
        Files.write(tmp, new File(profilePath, "datasources.py"), Charsets.UTF_8);

    }

    public void readConfig(DataSourcesConfigurationContainer ds) throws IOException {       
        ArrayList<DataSourcesConfiguration> dataSources = ds.get("dataSources");
        String tmp="";
        for (DataSourcesConfiguration d : dataSources) {
            profilePath = d.getProfilePath();           
            name = d.getName();
            jndiName = d.getJndiName();
            databaseName = d.getDatabaseName();
            driverType = d.getDriverType();
            serverName = d.getServerName();
            portNumber = d.getPortNumber();
            dataStoreHelperClassName = d.getDataStoreHelperClassName();
            componentManagedAuthenticationAlias = d.getComponentManagedAuthenticationAlias();
            xaRecoveryAuthAlias = d.getXaRecoveryAuthAlias();  
            tmp+="nodeList = AdminTask.listJDBCProviders().split('\\r\\n')\n";
            tmp+="jdbcProvider = nodeList[len(nodeList)-1]\n";
            tmp+="AdminTask.createDatasource(jdbcProvider , ['-name', '"+name+"', '-jndiName', '"+jndiName+"', '-configureResourceProperties', '[[databaseName java.lang.String "+databaseName+"] [driverType java.lang.Integer "+driverType+"] [serverName java.lang.String  "+serverName+"] [portNumber java.lang.Integer "+portNumber+"]]', '-dataStoreHelperClassName', '"+dataStoreHelperClassName+"', '-componentManagedAuthenticationAlias', '"+componentManagedAuthenticationAlias+"', '-xaRecoveryAuthAlias', '"+xaRecoveryAuthAlias+"'])\n";
        }

        tmp+="AdminConfig.save()\n";  
        tmp+="print 'Successfully configured data sources.'\n";
        Files.write(tmp, new File(profilePath, "datasources.py"), Charsets.UTF_8);      

        tmp+="AdminConfig.save()\n";
        Files.write(tmp, new File(profilePath, "datasources.py"), Charsets.UTF_8);

    }
  
    
    public int executeScript() throws IOException{
        int exitVal=-1;
        String bat = "wsadmin.bat -lang jython -profile datasources.py\n";
        File batchFile = new File("was_ds.cmd");
        Files.write(bat, batchFile , Charsets.UTF_8); 
        
        ProcessBuilder builder = new ProcessBuilder(batchFile.getAbsolutePath());   
        builder.directory(new File(profilePath));
        log.info("execute command ({}) to configure data sources at {}", builder.command(), profilePath);
        Process process = builder.start();
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new IllegalArgumentException("Failed to configure data sources, status code is " + exitVal);
            }            
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {            
            process.destroy();     
        }   
        return exitVal;
    }

   
    public int configure(File config) throws IOException{
        readConfigAndCreateScript(config);
        int exitVal = executeScript();
        return exitVal;
    }  
*/    
    public String returnScript(WebSphereConfiguration wasConfig, CommonDSConfiguration commonDSConfig, DataSourcesConfigurationContainer ds) {        
        ArrayList<DataSourcesConfiguration> dataSources = ds.get("dataSources");
        String tmp="";
        for (DataSourcesConfiguration d : dataSources) {               
            name = d.getName();
            jndiName = d.getJndiName();
            databaseName = commonDSConfig.getDatabaseName();
            driverType = commonDSConfig.getDriverType();
            serverName = wasConfig.getServerName();
            portNumber = commonDSConfig.getPortNumber();
            dataStoreHelperClassName = commonDSConfig.getDataStoreHelperClassName();
            componentManagedAuthenticationAlias = d.getComponentManagedAuthenticationAlias();
            xaRecoveryAuthAlias = d.getXaRecoveryAuthAlias();  
            tmp+="nodeList = AdminTask.listJDBCProviders().split('\\r\\n')\n";
            tmp+="jdbcProvider = nodeList[len(nodeList)-1]\n"; 
            
            tmp+="dsId = \"\"\n";
            tmp+="dsList = AdminConfig.getid(\"/DataSource:" + name + "\")\n";
            tmp+="if (len(dsList) > 0):\n";
            tmp+="  for item in dsList.split('\\n'):\n";
            tmp+="      item = item.rstrip()\n";
            tmp+="      jndiName = AdminConfig.showAttribute(item, \"jndiName\" )\n";
            tmp+="      if (\""+jndiName+"\" == jndiName):\n";
            tmp+="          dsId = item\n";
            tmp+="if (dsId == \"\"):\n";          
            tmp+="  AdminTask.createDatasource(jdbcProvider , ['-name', '"+name+"', '-jndiName', '"+jndiName+"', '-configureResourceProperties', '[[databaseName java.lang.String "+databaseName+"] [driverType java.lang.Integer "+driverType+"] [serverName java.lang.String  "+serverName+"] [portNumber java.lang.Integer "+portNumber+"]]', '-dataStoreHelperClassName', '"+dataStoreHelperClassName+"', '-componentManagedAuthenticationAlias', '"+componentManagedAuthenticationAlias+"', '-xaRecoveryAuthAlias', '"+xaRecoveryAuthAlias+"'])\n";            
            tmp+="else:\n";
            tmp+="  print \""+name+" already exists in this JDBC Provider!\"\n";
            tmp+="  print \"Removing existing data source and creating a new one\"\n";
            tmp+="  AdminConfig.remove(dsId)\n";
            tmp+="  AdminConfig.save()\n";            
            tmp+="  AdminTask.createDatasource(jdbcProvider , ['-name', '"+name+"', '-jndiName', '"+jndiName+"', '-configureResourceProperties', '[[databaseName java.lang.String "+databaseName+"] [driverType java.lang.Integer "+driverType+"] [serverName java.lang.String  "+serverName+"] [portNumber java.lang.Integer "+portNumber+"]]', '-dataStoreHelperClassName', '"+dataStoreHelperClassName+"', '-componentManagedAuthenticationAlias', '"+componentManagedAuthenticationAlias+"', '-xaRecoveryAuthAlias', '"+xaRecoveryAuthAlias+"'])\n";            
            
        }
        tmp+="print 'Successfully configured data sources.'\n";
        return tmp;
    }
 /*  
    public String returnScript(File configFile) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        DataSourcesConfigurationContainer ds = mapper.readValue(configFile,DataSourcesConfigurationContainer.class);
        ArrayList<DataSourcesConfiguration> dataSources = ds.get("dataSources");
        String tmp="";
        for (DataSourcesConfiguration d : dataSources) {                       
            name = d.getName();
            jndiName = d.getJndiName();
            databaseName = d.getDatabaseName();
            driverType = d.getDriverType();
            serverName = d.getServerName();
            portNumber = d.getPortNumber();
            dataStoreHelperClassName = d.getDataStoreHelperClassName();
            componentManagedAuthenticationAlias = d.getComponentManagedAuthenticationAlias();
            xaRecoveryAuthAlias = d.getXaRecoveryAuthAlias();  
            tmp+="nodeList = AdminTask.listJDBCProviders().split('\\r\\n')\n";
            tmp+="jdbcProvider = nodeList[len(nodeList)-1]\n";            
            tmp+="AdminTask.createDatasource(jdbcProvider , ['-name', '"+name+"', '-jndiName', '"+jndiName+"', '-configureResourceProperties', '[[databaseName java.lang.String "+databaseName+"] [driverType java.lang.Integer "+driverType+"] [serverName java.lang.String  "+serverName+"] [portNumber java.lang.Integer "+portNumber+"]]', '-dataStoreHelperClassName', '"+dataStoreHelperClassName+"', '-componentManagedAuthenticationAlias', '"+componentManagedAuthenticationAlias+"', '-xaRecoveryAuthAlias', '"+xaRecoveryAuthAlias+"'])\n";            
        }
        tmp+="print 'Successfully configured data sources.'\n";
        return tmp;
    }
    
    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Configure Data Sources", "");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Configure Data Sources", "");
        daemon.start();
    }
    */
}
