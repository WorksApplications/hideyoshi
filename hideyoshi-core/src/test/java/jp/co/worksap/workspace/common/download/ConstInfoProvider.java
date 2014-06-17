package jp.co.worksap.workspace.common.download;

import java.io.IOException;

import javax.annotation.Nonnull;

import lombok.AllArgsConstructor;

@AllArgsConstructor
final class ConstInfoProvider implements AuthenticationInfoProvider {
    @Nonnull
    private final String userName;
    @Nonnull
    private final String password;

    @Override
    public String loadUserName() throws IOException {
        return userName;
    }

    @Override
    public String loadPassword() throws IOException {
        return password;
    }

}
