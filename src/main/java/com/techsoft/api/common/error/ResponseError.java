package com.techsoft.api.common.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class ResponseError {
    Date timestamp;
    Integer status;
    String error;
    String message;
}
