package com.cotiviti.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtTokenConstants {

    TOKEN_PREFIX("Bearer "),
    USER("User"),
    AUTH("Auth"),
    TICKET_RESERVATION_SYSTEM("Ticket Reservation System"),
    USER_ID("userId"),
    USERNAME("username"),
    GROUP("group"),
    ROLES("roles"),
    IS_TEMP("isTemp");

    private String name;

}
