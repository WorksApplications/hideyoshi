package jp.co.worksap.workspace.database.db2;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public final class Node {
    private String node;
    private String remote;
    private String server;
    private String remote_instance;
    private String system;
    private String ostype;

    @Nonnull
    public String catalogCommand() {
        return "CATALOG TCPIP NODE " + this.getNode() + " REMOTE " + this.getRemote() + " SERVER " + this.getServer() + " REMOTE_INSTANCE  " + this.getRemote_instance() + " SYSTEM  " + this.getSystem() + " OSTYPE " + this.getOstype();
    }
}
