package jp.co.worksap.workspace.common;

import java.io.IOException;

import jp.co.worksap.workspace.common.download.AuthenticationInfoProvider;

public final class NeverCalledProvider implements AuthenticationInfoProvider {

    @Override
    public String loadUserName() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String loadPassword() throws IOException {
        throw new UnsupportedOperationException();
    }

}
