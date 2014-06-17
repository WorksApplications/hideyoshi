package jp.co.worksap.workspace.common.download;

import java.io.IOException;

public interface AuthenticationInfoProvider {
    String loadUserName() throws IOException;
    String loadPassword() throws IOException;
}
