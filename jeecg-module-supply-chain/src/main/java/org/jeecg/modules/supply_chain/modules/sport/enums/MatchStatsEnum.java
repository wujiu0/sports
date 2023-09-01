package org.jeecg.modules.supply_chain.modules.sport.enums;

import lombok.Getter;

@Getter
public enum MatchStatsEnum {

    NOT_STARTED(-1),
    PROGRESS(0),
    FINISHED(1);

    private final int value;

    MatchStatsEnum(int value) {
        this.value = value;
    }
}
