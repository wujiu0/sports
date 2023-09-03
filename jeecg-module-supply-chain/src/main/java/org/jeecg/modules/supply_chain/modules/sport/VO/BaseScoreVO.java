package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseScoreVO {
    @ApiModelProperty(value = "总分")
    private Integer total;

    @ApiModelProperty(value = "x环次数")
    private Integer xCount;

    @ApiModelProperty(value = "10环次数")
    private Integer tenCount;
}
