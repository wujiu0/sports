package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InitInfoVO {

    @ApiModelProperty("轮次")
    private Integer round;
    @ApiModelProperty("当前组别")
    private Integer group;

    @ApiModelProperty("状态 0报名未开始 1报名中 2报名结束-抽签中 3抽签完成-比赛进行中 4比赛已结束")
    private Integer status;

    @ApiModelProperty("成绩是否已经录入")
    private Boolean insertFlag;
}
