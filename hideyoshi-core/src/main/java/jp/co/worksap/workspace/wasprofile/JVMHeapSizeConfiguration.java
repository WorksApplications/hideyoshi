package jp.co.worksap.workspace.wasprofile;

import javax.annotation.ParametersAreNonnullByDefault;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JVMHeapSizeConfiguration {
    private String maxHeapSize;
    private String initHeapSize;

    @ParametersAreNonnullByDefault
    public String returnScript(String server, String node){
        StringBuilder script = new StringBuilder();

        script.append("AdminTask.setJVMMaxHeapSize('-serverName ").append(server)
                .append(" -nodeName ").append(node)
                .append(" -maximumHeapSize ").append(maxHeapSize)
                .append("')\n");

        script.append("AdminTask.setJVMInitialHeapSize('-serverName ").append(server)
                .append(" -nodeName ").append(node)
                .append(" -initialHeapSize ").append(initHeapSize)
                .append("')\n");

        script.append("print 'Successfully configured JVM Heap Size.'\n");

        return script.toString();
    }
}
