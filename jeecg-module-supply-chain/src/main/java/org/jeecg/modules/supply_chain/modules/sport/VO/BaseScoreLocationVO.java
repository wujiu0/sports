package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseScoreLocationVO {

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("个人总成绩")
    private Integer total;

}
