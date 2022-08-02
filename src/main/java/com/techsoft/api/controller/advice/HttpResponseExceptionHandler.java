package com.techsoft.api.controller.advice;

import com.techsoft.api.common.error.HttpResponseException;
import com.techsoft.api.common.error.ResponseError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class HttpResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpResponseException.class)
    public ResponseEntity<ResponseError> handleException(HttpResponseException e) {
        log.warn("Error status code: {}", e.getHttpStatus().value());
        return new ResponseEntity<>(e.getError(), e.getHttpStatus());
    }
}
