package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScoreLocationOverviewVO extends BaseScoreVO {
    @ApiModelProperty("排名")
    private Integer rank;

    @ApiModelProperty("单位")
    private String location;

    @ApiModelProperty("人员成绩")
    private List<BaseScoreLocationVO> scoreLocationVOList;


}
