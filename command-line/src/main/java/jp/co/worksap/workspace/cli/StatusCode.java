package jp.co.worksap.workspace.cli;

import lombok.Getter;

public enum StatusCode {
    NORMAL(0), ERROR(1);

    @Getter
    private int code;

    StatusCode(int code) {
        this.code = code;
    }
}
