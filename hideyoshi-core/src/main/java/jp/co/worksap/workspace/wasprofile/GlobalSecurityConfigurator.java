package jp.co.worksap.workspace.wasprofile;

import java.util.List;
import java.util.Map;

public class GlobalSecurityConfigurator {
    public String returnScript(WebSphereProfileConfiguration wasConfig){
        Map<String,List<GlobalSecurityConfiguration>> config = wasConfig.getGlobalSecurity();
        if (config == null) {
            return "";
        }

        StringBuilder script = new StringBuilder();

        for (GlobalSecurityConfiguration g : config.get("data")) {
            String cell = wasConfig.getCellName();
            String alias = g.getAlias();
            String userid = g.getUserid();
            String password = g.getPassword();
            script.append("security = AdminConfig.getid('/Cell:"+cell+"/Security:/')\n");
            script.append("alias = ['alias', '"+alias+"']\n");
            script.append("userid = ['userId', '"+userid+"']\n");
            script.append("password = ['password', '"+password+"']\n");
            script.append("jaasAttrs = [alias, userid, password]\n");
            script.append("jaasAuthDataList = AdminConfig.list('JAASAuthData')\n"); 
            script.append("if len(jaasAuthDataList) > 0:\n");
            script.append("  jaasAuthDataList=jaasAuthDataList.split(lineSeparator)\n");
            script.append("  for jaasAuthId in jaasAuthDataList:\n");
            script.append("      getAlias = AdminConfig.showAttribute(jaasAuthId, 'alias')\n");
            script.append("      if (cmp(getAlias,\""+alias+"\") == 0):\n");
            script.append("          print ' JAASAuthData exists with name :'+ \""+alias+"\"\n");
            script.append("          print ' Removing JAASAuthData with name :'+ \""+alias+"\"+ 'and creating a new one...'\n");
            script.append("          AdminConfig.remove(jaasAuthId)\n");
            script.append("          print ' JAASAuthData removed '\n");
            script.append("          AdminConfig.save()\n");   
            script.append("AdminConfig.create('JAASAuthData', security, jaasAttrs)\n");
        }
        return script.toString();
    }
}
