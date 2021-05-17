package com.cotiviti.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusConstant {

    CREATE_APPROVE("CREATE_APPROVE", "ACTIVE"),
    BLOCKED_APPROVE("BLOCKED_APPROVE", "BLOCKED");

    private final String name;
    private final String description;
}
