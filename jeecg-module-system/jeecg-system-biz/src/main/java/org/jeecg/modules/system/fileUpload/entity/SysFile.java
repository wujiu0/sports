package org.jeecg.modules.system.fileUpload.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_file")
@Accessors(chain = true)
public class SysFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String kid;
    @TableField("file_name")
    private String fileName;
    @TableField("extension")
    private String extension;
    @TableField("file_size")
    private Long fileSize;
    @TableField("file_path")
    private String filePath;
    @TableField("file_md5")
    private String fileMd5;
    @TableField("remark")
    private String remark;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
