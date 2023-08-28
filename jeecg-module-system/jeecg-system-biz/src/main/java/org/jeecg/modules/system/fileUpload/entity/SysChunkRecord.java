package org.jeecg.modules.system.fileUpload.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_chunk_record")
@Accessors(chain = true)
public class SysChunkRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String kid;
    @TableField("chunk_file_name")
    private String chunkFileName;
    @TableField("chunk_file_path")
    private String chunkFilePath;
    @TableField("file_md5")
    private String fileMd5;
    @TableField("chunk_file_md5")
    private String chunkFileMd5;
    @TableField("chunk")
    private Integer chunk;
    @TableField("total_chunk")
    private Integer totalChunk;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
