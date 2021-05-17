package com.cotiviti.utils;

import com.cotiviti.response.Response;
import org.springframework.http.HttpStatus;

public class ResponseUtility {

    public static Response getCreatedResponse(String message) {
        return Response.builder()
                .message(message)
                .success(true)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    public static Response getFailedResponse(String message) {
        return Response.builder()
                .success(false)
                .message(message)
                .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                .build();
    }

    public static Response getSuccessfulResponse(Object data, String message) {
        return Response.builder()
                .success(true)
                .message(message)
                .data(data)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    public static Response getSuccessfulResponse(String message) {
        return Response.builder()
                .success(true)
                .message(message)
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
