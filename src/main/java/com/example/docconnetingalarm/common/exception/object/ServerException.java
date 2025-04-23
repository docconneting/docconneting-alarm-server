package com.example.docconnetingalarm.common.exception.object;

import com.example.docconnetingalarm.common.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private ErrorCode errorCode;

    public ServerException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
