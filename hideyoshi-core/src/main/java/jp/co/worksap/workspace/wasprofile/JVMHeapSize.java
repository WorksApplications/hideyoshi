package jp.co.worksap.workspace.wasprofile;


public class JVMHeapSize {    
    private String server;
    private String node;
    private String maxHeapSize;
    private String initHeapSize;
/*
    public void readConfig(@Nonnull File configFile) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        JVMHeapSizeConfiguration config = mapper.readValue(configFile,JVMHeapSizeConfiguration.class);
        profilePath = config.getProfilePath();
        node = config.getNode();
        server = config.getServer();
        maxHeapSize = config.getMaxHeapSize();
        initHeapSize = config.getInitHeapSize();
    }
*/    
    public void readConfig(CommonWASConfiguration commonConfig, JVMHeapSizeConfiguration config){   
        server = commonConfig.getServerName();
        node = commonConfig.getNodeName();
        maxHeapSize = config.getMaxHeapSize();
        initHeapSize = config.getInitHeapSize();
    }
/*
    public void createScript() throws IOException {
        String tmp="";
        tmp+="AdminTask.setJVMMaxHeapSize('-serverName "+server+" -nodeName "+node+" -maximumHeapSize "+maxHeapSize+"')\n";
        tmp+="AdminTask.setJVMInitialHeapSize('-serverName "+server+" -nodeName "+node+" -initialHeapSize "+initHeapSize+"')\n";
        tmp+="AdminConfig.save()\n";
        tmp+="print 'Successfully configured JVM Heap Size.'\n";
        Files.write(tmp, new File(profilePath, "JVMHeapSize.py"), Charsets.UTF_8);
    }

    public int executeScript() throws IOException{
        int exitVal=-1;
        String bat = "wsadmin.bat -lang jython -profile JVMHeapSize.py\n";
        File batchFile = new File("was_jvm.cmd");
        Files.write(bat, batchFile , Charsets.UTF_8); 
        
        ProcessBuilder builder = new ProcessBuilder(batchFile.getAbsolutePath());   
        builder.directory(new File(profilePath));
        log.info("execute command ({}) to configure jvm heap size at {}", builder.command(), profilePath);
        Process process = builder.start();
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new IllegalArgumentException("Failed to configure jvm heap size, status code is " + exitVal);
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
        tmp+="AdminTask.setJVMMaxHeapSize('-serverName "+server+" -nodeName "+node+" -maximumHeapSize "+maxHeapSize+"')\n";
        tmp+="AdminTask.setJVMInitialHeapSize('-serverName "+server+" -nodeName "+node+" -initialHeapSize "+initHeapSize+"')\n";
        tmp+="print 'Successfully configured JVM Heap Size.'\n";
        return tmp;
    }
/*
    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "JVM Heap Size Configuration", "");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "JVM Heap Size Configuration", "");
        daemon.start();
    }
    */
}
