package org.jeecg.modules.supply_chain.modules.sport.VO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ParticipantInfVo {

    @ApiModelProperty(value = "participantId")
    private Integer participantId;

    @ApiModelProperty(value = "姓名")
    private String name;


    @ApiModelProperty(value = "单位")
    private String location;

    @ApiModelProperty(value = "靶子")
    private String targetName;

}
