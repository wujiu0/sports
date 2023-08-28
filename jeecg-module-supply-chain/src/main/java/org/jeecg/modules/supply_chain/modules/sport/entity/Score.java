package org.jeecg.modules.supply_chain.modules.sport.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("sports_scores")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Score对象", description = "成绩")
public class Score {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 参赛者id
     */
    @ApiModelProperty(value = "参赛者id")
    private Integer participantId;
    /**
     * 得分
     */
    @ApiModelProperty(value = "得分")
    private Integer score;
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
