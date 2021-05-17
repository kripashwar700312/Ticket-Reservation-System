package com.cotiviti.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@Setter
public class Response {

    private HttpStatus httpStatus;

    private boolean success;

    private String message;

    private Object data;

}
