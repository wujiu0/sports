package org.jeecg.modules.supply_chain.modules.sport.DTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ParticipantScoreDTO {


    /**
     * 参赛者id
     */
    @ApiModelProperty(value = "参赛者id")
    private Integer participantId;
    /**
     * 得分
     */
    @ApiModelProperty(value = "得分列表")
    private List<String> scoreList;
    /**
     * 轮次
     */
    @ApiModelProperty(value = "轮次")
    private Integer round;
    /**
     * 组号
     */
    @ApiModelProperty(value = "组号")
    private Integer group;
    /**
     * 箭序号
     */
    @ApiModelProperty(value = "箭序号")
    private Integer order;
    /**
     * 是否是x(0否1是)
     */
    @ApiModelProperty(value = "是否是x(0否 1是)")
    private Integer x;
}
