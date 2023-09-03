package org.jeecg.modules.supply_chain.modules.sport.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sport_score_overview")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "成绩总览", description = "成绩总览")
public class ScoreOverview {

    @ApiModelProperty("参赛者id")
    private Integer participantId;

    @ApiModelProperty("参赛者姓名")
    private String name;

    @ApiModelProperty("单位")
    private String location;

    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("组号")
    @TableField("`group`")
    private Integer group;

    @ApiModelProperty("排名")
    @TableField("`rank`")
    private Integer rank;

    @ApiModelProperty("总分")
    private Integer total;

    @ApiModelProperty("x环次数")
    private Integer xCount;

    @ApiModelProperty("10环次数")
    private Integer tenCount;
}
