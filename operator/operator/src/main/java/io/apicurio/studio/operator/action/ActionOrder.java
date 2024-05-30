package io.apicurio.studio.operator.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ActionOrder {

    ORDERING_FIRST(0),
    ORDERING_EARLY(1),
    ORDERING_DEFAULT(2),
    ORDERING_LATE(3),
    ORDERING_LAST(4);


    private final int value;
}
