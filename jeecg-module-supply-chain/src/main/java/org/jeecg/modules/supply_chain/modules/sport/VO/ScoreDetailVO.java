package org.jeecg.modules.supply_chain.modules.sport.VO;

import lombok.Data;

import java.util.List;

@Data
public class ScoreDetailVO extends BaseScoreVO {
    private List<Integer> scoreList;
}
