package com.cotiviti.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private String title;

    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final String title, final String message) {
        super(message);
        this.title = title;
    }
}
