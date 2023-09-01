package org.jeecg.modules.supply_chain.modules.sport.entity;


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

    @ApiModelProperty("状态 -1未开始 0进行中 1已结束")
    private Integer status;

    @ApiModelProperty("男子组靶子数量")
    private Integer maleTargetCount;

    @ApiModelProperty("女子组靶子数量")
    private Integer femaleTargetCount;


}
