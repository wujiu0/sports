package org.jeecg.modules.supply_chain.modules.sport.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("sport_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "配置信息")
public class Config {

    @ApiModelProperty("(主键)创建年份")
    private Integer createYear;

    @ApiModelProperty("状态 0报名未开始 1报名中 2报名结束-抽签中 3抽签完成-比赛进行中 4比赛已结束")
    private Integer status;

    @ApiModelProperty("比赛进度")
    private Integer progress;

    @ApiModelProperty("轮次")
    private Integer round;

    @ApiModelProperty("当前组")
    @TableField("`group`")
    private Integer group;

    @ApiModelProperty("男子组靶子数量")
    private Integer maleTargetCount;

    @ApiModelProperty("女子组靶子数量")
    private Integer femaleTargetCount;

    @ApiModelProperty("编辑开关 0关闭 1开启")
    private Integer modifyFlag;

}
