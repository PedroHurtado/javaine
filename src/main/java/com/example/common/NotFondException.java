package com.example.common;

public class NotFondException extends RuntimeException{
    public NotFondException(String message){
        this(message,null);
    }
    public NotFondException(String message, Throwable e){
        super(message,e);
    }
}
