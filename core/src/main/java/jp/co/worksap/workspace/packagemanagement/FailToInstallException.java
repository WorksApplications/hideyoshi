package jp.co.worksap.workspace.packagemanagement;

public class FailToInstallException extends RuntimeException {

    private static final long serialVersionUID = -4078115953392778271L;

    public FailToInstallException(String message) {
        super(message);
    }

    public FailToInstallException(String message, Throwable t) {
        super(message, t);
    }
}
