package jp.co.worksap.workspace.wasprofile;

import java.io.File;
import java.io.IOException;

import jp.co.worksap.workspace.common.PipingDaemon;
import lombok.extern.slf4j.Slf4j;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

@Slf4j
public class SharedLibrary { 
    private String cell;
    private String node;
    private String server;
    private String libName;
    private String libClassPath;
    private String clMode;
/*
    public void readConfig(File configFile) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        SharedLibraryConfiguration config = mapper.readValue(configFile,SharedLibraryConfiguration.class);
        profilePath = config.getProfilePath();
        cell = config.getCell();
        node = config.getNode();
        server = config.getServer();
        libName = config.getLibName();
        libClassPath = config.getLibClassPath();
        clMode = config.getClMode();
    }
 */   
    public void readConfig(CommonWASConfiguration commonConfig, SharedLibraryConfiguration config) {         
        cell = commonConfig.getCellName();
        node = commonConfig.getNodeName();
        server = commonConfig.getServerName();
        libName = config.getLibName();
        libClassPath = config.getLibClassPath();
        clMode = config.getClMode();
    }
/*
    public void createScript() throws IOException {
        String tmp="";
        tmp+="serv = AdminConfig.getid('/Cell:"+cell+"/Node:"+node+"/Server:"+server+"')\n";
        tmp+="AdminConfig.create('Library', serv, [['name', '"+libName+"'], ['classPath','"+libClassPath+"']])\n";
        tmp+="appServer = AdminConfig.list('ApplicationServer', serv)\n";
        tmp+="classLoad = AdminConfig.showAttribute(appServer, 'classloaders')\n";
        tmp+="cleanClassLoaders = classLoad[1:len(classLoad)-1]\n";
        tmp+="classLoader1 = cleanClassLoaders.split(' ')[0]\n";
        tmp+="classLoader1 = AdminConfig.create('Classloader', appServer, [['mode',  '"+clMode+"']])\n";
        tmp+="AdminConfig.create('LibraryRef', classLoader1, [['libraryName', 'MyshareLibrary']])\n";
        tmp+="AdminConfig.save()\n";
        tmp+="print 'Successfully configured shared library.'\n";
        Files.write(tmp, new File(profilePath, "sharedlib.py"), Charsets.UTF_8);
    }

  
    public int executeScript() throws IOException{
        int exitVal=-1;
        String bat = "wsadmin.bat -lang jython -profile sharedlib.py\n";        
        File batchFile = new File("was_sl.cmd");
        Files.write(bat, batchFile , Charsets.UTF_8); 
        
        ProcessBuilder builder = new ProcessBuilder(batchFile.getAbsolutePath());   
        builder.directory(new File(profilePath));
        log.info("execute command ({}) to configure shared library at {}", builder.command(), profilePath);
        Process process = builder.start();
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new IllegalArgumentException("Failed to configure shared library, status code is " + exitVal);
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
        tmp+="serv = AdminConfig.getid('/Cell:"+cell+"/Node:"+node+"/Server:"+server+"')\n";       
        tmp+="AdminConfig.create('Library', serv, [['name', '"+libName+"'], ['classPath','"+libClassPath+"']])\n";
        tmp+="appServer = AdminConfig.list('ApplicationServer', serv)\n";        
        tmp+="classLoad = AdminConfig.showAttribute(appServer, 'classloaders')\n";
        tmp+="cleanClassLoaders = classLoad[1:len(classLoad)-1]\n";
        tmp+="classLoader1 = cleanClassLoaders.split(' ')[0]\n";
        tmp+="classLoader1 = AdminConfig.create('Classloader', appServer, [['mode',  '"+clMode+"']])\n";
        tmp+="AdminConfig.create('LibraryRef', classLoader1, [['libraryName', 'MyshareLibrary']])\n";
        tmp+="print 'Successfully configured shared library.'\n";
        return tmp;
    }
    
/*
    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Shared Library Configuration", "");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Shared Library Configuration", "");
        daemon.start();
    }
    
    */
}

