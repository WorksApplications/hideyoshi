package jp.co.worksap.workspace.wasprofile;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import jp.co.worksap.workspace.common.PipingDaemon;
import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

@Slf4j
public class JDBCProvider {   
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
/*
    public void readConfig(@Nonnull File configFile) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        JDBCProviderConfiguration config = mapper.readValue(configFile,JDBCProviderConfiguration.class);
        profilePath = config.getProfilePath();
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
*/    
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
/*
    public void createScript() throws IOException {
        String tmp="";
        tmp+="AdminTask.createJDBCProvider(['-scope', '"+scope+"', '-databaseType', '"+databaseType+"', '-providerType', '"+providerType+"', '-implementationType', '"+implementationType+"', '-name', '"+name+"', '-description', '"+description+"', '-classpath', '"+classpath+"', '-nativePath', '"+nativePath+"', '-implementationClassName', '"+implementationClassName+"', '-isolated', '"+isolated+"'])\n"; 
        tmp+="AdminConfig.save()\n";
        tmp+="print 'Successfully configured jdbc provider. You can close this window now.'\n";
        Files.write(tmp, new File(profilePath, "jdbcProvider.py"), Charsets.UTF_8);
    }

    public int executeScript() throws IOException{
        int exitVal=-1;
        String bat = "wsadmin.bat -lang jython -profile jdbcProvider.py\n";
        File batchFile = new File("was_jdbc.cmd");
        Files.write(bat, batchFile , Charsets.UTF_8); 
        
        ProcessBuilder builder = new ProcessBuilder(batchFile.getAbsolutePath());   
        builder.directory(new File(profilePath));
        log.info("execute command ({}) to configure jdbc provider at {}", builder.command(), profilePath);
        Process process = builder.start();
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new IllegalArgumentException("Failed to configure jdbc provider, status code is " + exitVal);
            }            
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {            
            process.destroy();     
        }   
        return exitVal;
    }

    public int configure(File config) throws IOException{
        readConfig(config);
        createScript();
        int exitVal = executeScript();
        return exitVal;
    }
 */   
    public String returnScript(){
        String tmp="";
        tmp+="AdminTask.createJDBCProvider(['-scope', '"+scope+"', '-databaseType', '"+databaseType+"', '-providerType', '"+providerType+"', '-implementationType', '"+implementationType+"', '-name', '"+name+"', '-description', '"+description+"', '-classpath', '"+classpath+"', '-nativePath', '"+nativePath+"', '-implementationClassName', '"+implementationClassName+"', '-isolated', '"+isolated+"'])\n";        
        tmp+="print 'Successfully configured jdbc provider.'\n";
        return tmp;
    }
    
/*
    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "JDBC Provider Configuration", "");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "JDBC Provider Configuration", "");
        daemon.start();
    }
    
    */
}
