package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoreRoundDetailVO extends BaseScoreVO {
    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "单位")
    private String location;

    @ApiModelProperty(value = "靶子")
    private String targetName;

    @ApiModelProperty(value = "按组计分情况")
    private List<ScoreDetailVO> scorebyRoundList = new ArrayList<>();

}
