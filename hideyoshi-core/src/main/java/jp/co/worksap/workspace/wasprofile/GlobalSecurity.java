package jp.co.worksap.workspace.wasprofile;

import java.util.ArrayList;

public class GlobalSecurity {
    public String readConfigAndReturnScript(WebSphereConfiguration wasConfig, GlobalSecurityConfigurationContainer config){
        ArrayList<GlobalSecurityConfiguration> data = config.get("data");
        String tmp="";
        for (GlobalSecurityConfiguration g : data) {   
            String cell = wasConfig.getCellName();
            String alias = g.getAlias();
            String userid = g.getUserid();
            String password = g.getPassword();
            tmp+="security = AdminConfig.getid('/Cell:"+cell+"/Security:/')\n";
            tmp+="alias = ['alias', '"+alias+"']\n";
            tmp+="userid = ['userId', '"+userid+"']\n";
            tmp+="password = ['password', '"+password+"']\n";
            tmp+="jaasAttrs = [alias, userid, password]\n";
            tmp+="jaasAuthDataList = AdminConfig.list('JAASAuthData')\n"; 
            tmp+="if len(jaasAuthDataList) > 0:\n";
            tmp+="  jaasAuthDataList=jaasAuthDataList.split(lineSeparator)\n";
            tmp+="  for jaasAuthId in jaasAuthDataList:\n";
            tmp+="      getAlias = AdminConfig.showAttribute(jaasAuthId, 'alias')\n";
            tmp+="      if (cmp(getAlias,\""+alias+"\") == 0):\n";
            tmp+="          print ' JAASAuthData exists with name :'+ \""+alias+"\"\n";
            tmp+="          print ' Removing JAASAuthData with name :'+ \""+alias+"\"+ 'and creating a new one...'\n";
            tmp+="          AdminConfig.remove(jaasAuthId)\n";
            tmp+="          print ' JAASAuthData removed '\n";
            tmp+="          AdminConfig.save()\n";   
            tmp+="AdminConfig.create('JAASAuthData', security, jaasAttrs)\n";           
        }        
        return tmp;
    }
}
