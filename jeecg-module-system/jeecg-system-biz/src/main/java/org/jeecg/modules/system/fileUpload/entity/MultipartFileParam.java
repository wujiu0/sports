package org.jeecg.modules.system.fileUpload.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 文件上传请求参数
 */
@Data
@Accessors(chain = true)
public class MultipartFileParam {
    /**
     * 是否是分片文件
     */
    @NotNull(message = "是否分片不能为空")
    private Boolean chunkFlag;

    /**
     * 文件
     */
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    /**
     * 文件名
     */
    @NotNull(message = "文件名不能为空")
    private String fileName;

    /**
     * 文件总大小，单位是byte
     */
    private Long totalSize;

    /**
     * 文件md5
     */
    private String md5;

    // ——————————————chunkFlag为true时使用——————————————
    /**
     * 当前是第几块分片
     */
    private Integer chunk;

    /**
     * 总分片数量
     */
    private Integer totalChunk;

    /**
     * 分片文件md5
     */
    private String chunkMd5;
}
