package org.jeecg.modules.supply_chain.modules.sport.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ParticipantScoreDTO {
//    /**
//     * 靶子
//     */
//    @ApiModelProperty(value = "靶子")
//    private Target target;

    @ApiModelProperty("身份证号")
    private String idNumber;
    /**
     * 得分列表
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
    @TableField(value = "`group`")
    private Integer group;
}
