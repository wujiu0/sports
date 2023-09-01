package org.jeecg.modules.supply_chain.modules.sport.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Target {
    @ApiModelProperty(value = "靶号前缀")
    private Integer prefix;

    @ApiModelProperty(value = "靶号后缀1A 2B 3C 4D")
    private Integer suffix;
}
