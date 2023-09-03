package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ScoreMainOverviewVO extends BaseScoreVO {
    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "单位")
    private String location;

    @ApiModelProperty(value = "靶子")
    private String targetName;

    @ApiModelProperty("排名")
    private Integer rank;

    @ApiModelProperty(value = "按组总览")
    private List<BaseScoreOverviewVO> scoreRoundOverviewVOList;

}
