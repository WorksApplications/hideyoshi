package jp.co.worksap.workspace.cli;

import java.io.IOException;
import java.util.Scanner;

import jp.co.worksap.workspace.common.download.AuthenticationInfoProvider;

public final class SystemInAuthenticationInfoProvider implements
        AuthenticationInfoProvider {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String loadUserName() throws IOException {
        System.out.printf("User name:");
        String userName = scanner.next();
        System.out.println();
        return userName;
    }

    @Override
    public String loadPassword() throws IOException {
        System.out.printf("Password:");
        String password = scanner.next();
        System.out.println();
        return password;
    }

}
