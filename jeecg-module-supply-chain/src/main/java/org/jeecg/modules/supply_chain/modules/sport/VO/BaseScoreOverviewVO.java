package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseScoreOverviewVO {
    @ApiModelProperty("总分")
    private Integer total;

    @ApiModelProperty("排名")
    private Integer rank;
}