package jp.co.worksap.workspace.wasprofile;

import java.io.File;
import java.io.IOException;

import jp.co.worksap.workspace.common.PipingDaemon;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

@Slf4j
public class WebSphereProfileCreator {
    public int createAndConfigureProfile(WebSphereProfileConfiguration profile) throws IOException {
        int exitVal=0;
        String tmp="";
        String OS = System.getProperty("os.name"), platform;
        if(OS.indexOf("Win") >= 0){
            platform = "Windows";
        } else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ){
            platform = "Linux";
        } else{
            platform = "NotSupproted";
        }

        CreateProfile obj1 = new CreateProfile();
        obj1.readConfig(profile);
        String installPath = profile.getInstallPath();
        String profilePath = installPath+"\\WebSphere\\AppServer\\profiles\\"+profile.getProfileName()+"\\bin";
        tmp+="print 'Starting Configuration of WAS profile.....'\n";
        SharedLibraryConfigurator obj2 = new SharedLibraryConfigurator();
        obj2.readConfig(profile);
        tmp+="print 'Step 1 of 5: Configuring Shared Library.....'\n";
        tmp+=obj2.returnScript();

        JDBCProviderConfigurator obj3 = new JDBCProviderConfigurator();
        obj3.readConfig(profile.getJdbcProvider());
        tmp+="AdminConfig.save()\n";
        tmp+="print 'Step 2 of 5: Configuring JDBC Provider.....'\n";
        tmp+=obj3.returnScript();

        GlobalSecurityConfigurator obj4 = new GlobalSecurityConfigurator();
        tmp+="print 'Step 3 of 5: Configuring Global Security.....'\n";
        tmp+=obj4.returnScript(profile);

        DataSources obj5 = new DataSources();  
        tmp+="print 'Step 4 of 5: Configuring Data Sources .....'\n";
        tmp+=obj5.returnScript(profile);

        tmp+="print 'Step 5 of 5: Configuring JVM heap Size .....'\n";
        tmp+=profile.getJvmHeapSize().returnScript(profile.getServerName(), profile.getNodeName()); 
        tmp+="AdminConfig.save()\n";
        tmp+="print 'Configuration of WAS Profile is Complete.'\n";

        Files.write(tmp, new File(installPath, "configureProfile.py"), Charsets.UTF_8);
        String bat = "";
        bat+=obj1.returnScript();

        File batchFile1 = null, batchFile2=null;
        if(platform.equals("Windows")){
            batchFile1 = new File("was_create.cmd");
            Files.write(bat, batchFile1, Charsets.UTF_8);
            bat = "wsadmin.bat -lang jython -profile \""+installPath+"\\configureProfile.py\"\n";
            batchFile2 = new File("was_configure.cmd");
            Files.write(bat, batchFile2, Charsets.UTF_8);
        }else if(platform.equals("Linux")){
            batchFile1 = new File("was_create.sh");
            Files.write(bat, batchFile1, Charsets.UTF_8);
            bat = "wsadmin.sh -lang jython -profile \""+installPath+"\\configureProfile.py\"\n";
            batchFile2 = new File("was_configure.sh");
            Files.write(bat, batchFile2, Charsets.UTF_8);
        }
        else{
            log.info("Platform not supported");
            return -1;
        }

        File f = new File(profilePath);
        if(f.exists()){
            log.info("Profile already exists!! Deleting existing profile...");
            DeleteProfile objdel = new DeleteProfile(); 
            objdel.readConfig(profile);
            File batch1=null, batch2=null, batch3=null, batch4=null, batch5=null;
            if(platform.equals("Windows")){
                batch1 = new File("stopServer.cmd");
                Files.write(objdel.getStopServerScript(), batch1, Charsets.UTF_8);
                batch2 = new File("deleteProfile.cmd");
                Files.write(objdel.getDeleteProfileScript(), batch2, Charsets.UTF_8);
                batch3 = new File("updateRegistry.cmd");
                Files.write(objdel.getUpdateRegistryScript(), batch3, Charsets.UTF_8);
                batch4 = new File("deletingLogs.cmd");
                Files.write(objdel.getDeleteLogsScript(), batch4, Charsets.UTF_8);
                batch5 = new File("startServer.cmd");
                Files.write(objdel.getStartServerScript(), batch5, Charsets.UTF_8);
            }else if(platform.equals("Linux")){
                batch1 = new File("stopServer.sh");
                Files.write(objdel.getStopServerScript(), batch1, Charsets.UTF_8);
                batch2 = new File("deleteProfile.sh");
                Files.write(objdel.getDeleteProfileScript(), batch2, Charsets.UTF_8);
                batch3 = new File("updateRegistry.sh");
                Files.write(objdel.getUpdateRegistryScript(), batch3, Charsets.UTF_8);
                batch4 = new File("deletingLogs.sh");
                Files.write(objdel.getDeleteLogsScript(), batch4, Charsets.UTF_8);
                batch5 = new File("startServer.sh");
                Files.write(objdel.getStartServerScript(), batch5, Charsets.UTF_8);
            }else{
                log.info("Platform not supported");
                return -1;
            }

            if(batch1 != null){
                log.info("Stopping Server...");
                ProcessBuilder builder = new ProcessBuilder(batch1.getAbsolutePath());
                Process process = builder.start();
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    exitVal = process.waitFor();
                    if (exitVal != 0) {
                        throw new IllegalArgumentException("Failed to stop server, status code is " + exitVal);
                    }            
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }
            }

            if(batch2 != null){
                log.info("Deleting Profile...");
                ProcessBuilder builder = new ProcessBuilder(batch2.getAbsolutePath());
                Process process = builder.start();
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    exitVal = process.waitFor();
                    if (exitVal != 0) {
                        throw new IllegalArgumentException("Failed to delete profile, status code is " + exitVal);
                    }            
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }   
            }

            if(batch3 != null){
                log.info("Updating Registry...");
                ProcessBuilder builder = new ProcessBuilder(batch3.getAbsolutePath());
                Process process = builder.start();
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    exitVal = process.waitFor();
                    if (exitVal != 0) {
                        throw new IllegalArgumentException("Failed to update registry, status code is " + exitVal);
                    }            
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }
            }

            if(batch4 != null){
                log.info("Deleting Profile Logs...");
                ProcessBuilder builder = new ProcessBuilder(batch4.getAbsolutePath());
                Process process = builder.start();
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    exitVal = process.waitFor();
                    if (exitVal != 0) {
                        throw new IllegalArgumentException("Failed to delete logs, status code is " + exitVal);
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }
            }

            if(batch5 != null){
                log.info("Starting Server...");
                ProcessBuilder builder = new ProcessBuilder(batch5.getAbsolutePath());
                Process process = builder.start();
                try {
                    recordStdoutOf(process);
                    recordStderrOf(process);
                    process.getOutputStream().close();
                    exitVal = process.waitFor();
                    if (exitVal != 0) {
                        throw new IllegalArgumentException("Failed to start server, status code is " + exitVal);
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                } finally {
                    process.destroy();
                }
            }
            log.info("Existing profile deleted successfully. Creating New Profile...");
        }

        if(batchFile1 != null){
            ProcessBuilder builder = new ProcessBuilder(batchFile1.getAbsolutePath());
            log.info("execute command ({}) to create WAS profile at {}", builder.command(), profilePath);
            Process process = builder.start();
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                exitVal = process.waitFor();
                if (exitVal != 0) {
                    throw new IllegalArgumentException("Failed to create WAS, status code is " + exitVal);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
            }
        }

        if(batchFile2 != null){
            ProcessBuilder builder = new ProcessBuilder(batchFile2.getAbsolutePath()); 
            builder.directory(new File(profilePath));
            log.info("execute command ({}) to configure WAS profile at {}", builder.command(), profilePath);
            Process process = builder.start();
            try {
                recordStdoutOf(process);
                recordStderrOf(process);
                process.getOutputStream().close();
                exitVal = process.waitFor();
                if (exitVal != 0) {
                    throw new IllegalArgumentException("Failed to configure WAS, status code is " + exitVal);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } finally {
                process.destroy();
            }
        }
        return exitVal;
    }

    private void recordStdoutOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getInputStream(), "WebSphere Configuration", "");
        daemon.start();
    }

    private void recordStderrOf(final Process process) throws IOException {
        Thread daemon = PipingDaemon.createThread(process.getErrorStream(), "WebSphere Configuration", "");
        daemon.start();
    }
}
