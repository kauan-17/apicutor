package com.apicultor.apicutor.vo;

public class ApiErrorVO {
    private String message;
    private String error;

    public ApiErrorVO() {}

    public ApiErrorVO(String message) {
        this.message = message;
        this.error = message;
    }

    public ApiErrorVO(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

