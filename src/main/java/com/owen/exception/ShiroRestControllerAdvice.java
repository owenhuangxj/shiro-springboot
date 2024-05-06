package com.owen.exception;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ShiroRestControllerAdvice {
    @ExceptionHandler(AuthorizationException.class)
    public String handleAuthorizationException(AuthorizationException exception) {
        exception.printStackTrace();
        return exception.getMessage() == null ?"AuthorizationException>>>>>>" : exception.getMessage();
    }
}
