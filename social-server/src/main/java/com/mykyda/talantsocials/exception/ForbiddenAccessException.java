package com.mykyda.talantsocials.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String s) {
        super(s);
    }
}
