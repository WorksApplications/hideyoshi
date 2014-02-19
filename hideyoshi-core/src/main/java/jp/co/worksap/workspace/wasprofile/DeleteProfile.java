package jp.co.worksap.workspace.wasprofile;

public class DeleteProfile {    
    private String profileName;
    private String installPath;
    private String serverName;
    
    public void readConfig(CreateProfileConfiguration config, CommonWASConfiguration commonConfig){
        
        profileName = config.getProfileName();
        serverName = commonConfig.getServerName();
        installPath = config.getInstallPath();
    }
    
    public String getStopServerScript(){
        String tmp="";        
        tmp+="cd "+installPath+"\\WebSphere\\AppServer\\bin\n";
        tmp+="stopServer "+serverName+"\n";            
        return tmp;
        
    }
    
    public String getDeleteProfileScript(){
        String tmp="";  
        tmp+="cd "+installPath+"\\WebSphere\\AppServer\\bin\n";
        tmp+="manageprofiles -delete -profileName "+profileName+"\n";             
        return tmp;
    }
    
    public String getUpdateRegistryScript(){
        String tmp="";  
        tmp+="cd "+installPath+"\\WebSphere\\AppServer\\bin\n";
        tmp+="manageprofiles -validateAndUpdateRegistry\n";           
        return tmp;
    }
    
    public String getDeleteLogsScript(){
        String tmp="";  
        tmp+="rmdir /s /q \""+installPath+"\\WebSphere\\AppServer\\profiles\\"+profileName+"\"\n";           
        return tmp;
    }
 
    
    public String getStartServerScript(){
        String tmp="";          
        tmp+="cd "+installPath+"\\WebSphere\\AppServer\\bin\n";
        tmp+="startServer "+serverName+"\n";  ;       
        return tmp;
    }
    
    
}
