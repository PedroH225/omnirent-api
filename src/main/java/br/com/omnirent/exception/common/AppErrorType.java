package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public interface AppErrorType {
	String getErrorType();
    String getErrorCode();
    String getMessageKey();
    HttpStatus getHttpCode();
}
