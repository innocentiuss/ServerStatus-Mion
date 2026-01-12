package com.bubble.status.exceptions;

import org.springframework.http.HttpStatus;


public class CommonException extends RuntimeException{
    private String message;
    private int httpCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

    public CommonException(String message) {
        super(message);
        this.message = message;
    }

    public CommonException(String message, int httpCode) {
        super(message);
        this.message = message;
        this.httpCode = httpCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
