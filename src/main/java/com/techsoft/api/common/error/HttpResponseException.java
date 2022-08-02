package com.techsoft.api.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Slf4j
@Getter
@AllArgsConstructor
public class HttpResponseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public ResponseError getError() {
        return new ResponseError(new Date(), httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }
}