package jp.co.worksap.workspace.wasprofile;

import lombok.Getter;

@Getter
public class CreateProfile {
    private String installPath;
    private String profileName;
    private String nodeName;
    private String cellName;

    public void readConfig(WebSphereProfileConfiguration wasConfig) {
        installPath = wasConfig.getInstallPath();
        profileName = wasConfig.getProfileName();
        nodeName =  wasConfig.getNodeName();
        cellName =  wasConfig.getCellName();
    }

    public String returnScript(){
        String bat = "cd "+installPath+"\\WebSphere\\AppServer\\bin\n";
      
        bat += "manageprofiles -create -profileName "+profileName+" -profilePath \""+installPath+"\\WebSphere\\AppServer\\profiles\\"+profileName+"\" -templatePath \""+installPath+"\\WebSphere\\AppServer\\profileTemplates\\default"+"\" -nodeName  "+nodeName+" -cellName "+cellName+" -isDefault -defaultPorts\n";
        return bat;
    }
}
