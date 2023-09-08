package org.jeecg.modules.supply_chain.modules.sport.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 参赛人员
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
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
    private String name;
    /**
     * 性别: 0女 1男
     */
    @ApiModelProperty(value = "性别: 0女 1男")
    @Excel(name = "性别", width = 15, replace = {"女_0", "男_1"})
    private Integer sex;
    /**
     * 出生年月
     */
    @ApiModelProperty(value = "出生年月")
    @Excel(name = "出生年月", width = 15, format = "yyyy-MM")
    private Date birthday;
    /**
     * 身份证号
     */
    @ApiModelProperty(value = "身份证号")
    @Excel(name = "身份证号", width = 20)
    private String idNumber;
    /**
     * 民族
     */
    @ApiModelProperty(value = "民族")
    @Excel(name = "民族", width = 15)
    private String nation;
    /**
     * 人员类别
     */
    @ApiModelProperty(value = "人员类别")
    @Excel(name = "人员类别", width = 15)
    private String category;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
//    @Excel(name = "备注", width = 20)
    private String notes;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    @Excel(name = "单位", width = 15)
    private String location;
    /**
     * 靶子序号
     */
    @ApiModelProperty(value = "靶子序号")
//    @Excel(name = "靶子序号", width = 15)
    private Integer target;
    /**
     * 组内位次
     */
    @ApiModelProperty(value = "组内位次")
//    @Excel(name = "组内位次", width = 15)
    private Integer indexInTarget;

    @ApiModelProperty(value = "报名时间")
//    @Excel(name = "报名时间", width = 30)
    private LocalDateTime createTime;
}
