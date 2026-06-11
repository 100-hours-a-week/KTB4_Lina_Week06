package com.community.api.common;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message){
        super(message);
    }
}
