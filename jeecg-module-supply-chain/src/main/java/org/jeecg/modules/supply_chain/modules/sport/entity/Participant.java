package org.jeecg.modules.supply_chain.modules.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 参赛人员
 * @Author: jeecg-boot
 * @Date: 2023-05-18
 * @Version: V1.0
 */
@Data
@TableName("sport_participant")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Participant对象", description = "参赛人员")
public class Participant implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Integer id; // 主键
    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String name;
    /**
     * 性别: 0女 1男
     */
    @ApiModelProperty(value = "性别: 0女 1男")
    private Integer sex;
    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    private Date birthday;
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    private String idNumber;
    /**
     * 民族
     */
    @ApiModelProperty(value = "民族")
    private String nation;
    /**
     * 人员类别
     */
    @ApiModelProperty(value = "人员类别")
    private String category;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String notes;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String location;
    /**
     * 靶子序号
     */
    @ApiModelProperty(value = "靶子序号")
    private Integer target;
}
