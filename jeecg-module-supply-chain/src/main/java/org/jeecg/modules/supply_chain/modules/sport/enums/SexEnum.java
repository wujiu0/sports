package org.jeecg.modules.supply_chain.modules.sport.enums;

import lombok.Getter;

@Getter
public enum SexEnum {

    FeMALE(0),
    MALE(1);

    private final int value;

    SexEnum(int value) {
        this.value = value;
    }
}