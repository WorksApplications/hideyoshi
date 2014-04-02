package jp.co.worksap.workspace.wasprofile;

public class SharedLibraryConfigurator {
    private String cell;
    private String node;
    private String server;
    private String libName;
    private String libClassPath;
    private String clMode;

    public void readConfig(WebSphereConfiguration wasConfig) {
        cell = wasConfig.getCellName();
        node = wasConfig.getNodeName();
        server = wasConfig.getServerName();
        libName = wasConfig.getSharedLibrary().getLibName();
        libClassPath = wasConfig.getSharedLibrary().getLibClassPath();
        clMode = wasConfig.getSharedLibrary().getClMode();
    }

    public String returnScript() {
        StringBuilder script = new StringBuilder();
        script.append("serv = AdminConfig.getid('/Cell:").append(cell).append("/Node:").append(node).append("/Server:" + server + "')\n");
        script.append("AdminConfig.create('Library', serv, [['name', '").append(libName).append("'], ['classPath','").append(libClassPath).append("']])\n");
        script.append("appServer = AdminConfig.list('ApplicationServer', serv)\n");
        script.append("classLoad = AdminConfig.showAttribute(appServer, 'classloaders')\n");
        script.append("cleanClassLoaders = classLoad[1:len(classLoad)-1]\n");
        script.append("classLoader1 = cleanClassLoaders.split(' ')[0]\n");
        script.append("classLoader1 = AdminConfig.create('Classloader', appServer, [['mode',  '").append(clMode).append("']])\n");
        script.append("AdminConfig.create('LibraryRef', classLoader1, [['libraryName', 'MyshareLibrary']])\n");
        script.append("print 'Successfully configured shared library.'\n");
        return script.toString();
    }
}
