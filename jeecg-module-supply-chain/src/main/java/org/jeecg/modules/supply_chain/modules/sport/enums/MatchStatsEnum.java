package org.jeecg.modules.supply_chain.modules.sport.enums;

import lombok.Getter;

@Getter
public enum MatchStatsEnum {

    NOT_STARTED(0, "报名未开始"),
    PROGRESS(1, "报名中"),
    GROUPING(2, "报名结束-抽签中"),
    MATCHING(3, "抽签完成-比赛进行中"),
    FINISHED(4, "比赛已结束");


    private final int value;
    private final String desc;

    MatchStatsEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
