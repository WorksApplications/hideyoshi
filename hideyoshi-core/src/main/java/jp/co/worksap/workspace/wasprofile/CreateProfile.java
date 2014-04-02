package jp.co.worksap.workspace.wasprofile;

import lombok.Getter;

@Getter
public class CreateProfile {
    private String installPath;
    private String profileName;
    private String nodeName;
    private String cellName;
/*
    public void readConfig(File configFile) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();
        CreateProfileConfiguration config = mapper.readValue(configFile,CreateProfileConfiguration.class);
        installPath = config.getInstallPath();
        profileName = config.getProfileName();
        nodeName = config.getNodeName();
        cellName = config.getCellName();      
    }
    */
    public void readConfig(WebSphereProfileConfiguration wasConfig) {
        installPath = wasConfig.getInstallPath();
        profileName = wasConfig.getProfileName();
        nodeName =  wasConfig.getNodeName();
        cellName =  wasConfig.getCellName();
    }

    /*
    public int executeScript() throws IOException{
        int exitVal=-1;
        String bat = "manageprofiles -create -profileName "+profileName+" -profilePath \""+installPath+"\\WebSphere\\AppServer\\profiles\\"+profileName+"\" -templatePath \""+installPath+"\\WebSphere\\AppServer\\profileTemplates\\default"+"\" -nodeName  "+nodeName+" -cellName "+cellName+" -isDefault -defaultPorts\n";        
        File batchFile = new File("was_profile.cmd");
        Files.write(bat, batchFile , Charsets.UTF_8);         
                
        ProcessBuilder builder = new ProcessBuilder(batchFile.getAbsolutePath());   
        builder.directory(new File(installPath+"\\WebSphere\\AppServer\\bin"));
        log.info("execute command ({}) to create WAS profile at {}", builder.command(), installPath);
        Process process = builder.start();
        try {
            recordStdoutOf(process);
            recordStderrOf(process);
            process.getOutputStream().close();
            exitVal = process.waitFor();
            if (exitVal != 0) {
                throw new IllegalArgumentException("Failed to create WAS profile, status code is " + exitVal);
            }            
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {            
            process.destroy();     
        }   
        return exitVal;
    }


    public int create(File config) throws IOException{
        readConfig(config);        
        int exitVal = executeScript();
        return exitVal;
    }
*/
    
    
    public String returnScript(){        
        String bat = "cd "+installPath+"\\WebSphere\\AppServer\\bin\n";
      
        bat += "manageprofiles -create -profileName "+profileName+" -profilePath \""+installPath+"\\WebSphere\\AppServer\\profiles\\"+profileName+"\" -templatePath \""+installPath+"\\WebSphere\\AppServer\\profileTemplates\\default"+"\" -nodeName  "+nodeName+" -cellName "+cellName+" -isDefault -defaultPorts\n";
        return bat;
    }
 /*   
    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "Create WAS Profile", "");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "Create WAS Profile", "");
        daemon.start();
    }
*/
}
