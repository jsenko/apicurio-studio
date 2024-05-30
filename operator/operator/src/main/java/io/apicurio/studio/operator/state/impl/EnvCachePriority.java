package io.apicurio.studio.operator.state.impl;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum EnvCachePriority {

    OPERATOR_LOW(0),
    SPEC_HIGH(1);

    private final int priority;

    EnvCachePriority(int priority) {
        this.priority = priority;
    }
}
