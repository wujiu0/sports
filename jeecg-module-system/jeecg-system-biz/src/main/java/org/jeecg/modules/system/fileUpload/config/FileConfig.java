package org.jeecg.modules.system.fileUpload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(value = "file")
public class FileConfig {
    private String uploadDir;
    private Integer chunkSize;
}
