package org.jeecg.modules.system.fileUpload.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UploadResultVo {

    /**
     * 是否已上传完毕
     */
    private Boolean uploaded = false;
    /**
     * 文件kid
     */
    private String fileKid;
    /**
     * 已上传分片
     */
    private List<Integer> chunkNum;
}
