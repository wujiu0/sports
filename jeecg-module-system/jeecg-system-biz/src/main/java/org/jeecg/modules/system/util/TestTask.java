package org.jeecg.modules.system.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author
 * @Date
 * @Version 1.0
 * @Description 定时备份数据库
 */
@Configuration
@EnableScheduling
@Slf4j
public class TestTask {
    @Value("${backup.dump.util}")
    private String dumpUtil;
    @Value("${backup.dump.path}")
    private String dumpPath;
    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String url;
    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String username;
    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String password;

    //@Scheduled(cron = "0/5 * * * * ?")
//    @Scheduled(cron = "0 0/2 * * * ?")
//    @Scheduled(cron = "0 0 */1 * * ?")
    private void configureTasks() {
        checkDumpUtil();
        URI uri = URI.create(url.substring(5));
        String host = uri.getHost();
        String dataBaseName = uri.getPath().substring(1);
        String filename = System.currentTimeMillis() + "_" + "dump.sql";

        StringBuilder backupSql = new StringBuilder();
        backupSql.append(dumpUtil).append(" --opt").append(" -h ").append(host).append(" --user=").append(username).append(" --password=").append(password);
        backupSql.append(" --result-file=").append(dumpPath).append(filename).append(" --default-character-set=utf8 ").append(dataBaseName);
        log.info("backup sql: {} ", backupSql);
        try {
            Process exec = Runtime.getRuntime().exec(backupSql.toString());
            // 如果需要控制执行时间, 可调用 public boolean waitFor(long timeout, TimeUnit unit)
            int code = exec.waitFor();
            if (code == 0) {
                log.info("数据库备份成功，保存路径：" + dumpPath);
            } else {
                log.info("数据库备份失败, 错误码: {}", code);
            }
        } catch (Exception e) {
            log.error("数据库备份失败!", e);
        }
    }

    private void checkDumpUtil() {

        String os = System.getProperty("os.name").toLowerCase();
        // 简单判断下操作系统, 如果是spring中定时任务执行的类是单例的,
        // 需要加上!backupBinPath.contains(".exe")避免每次执行都拼接后缀
        if (os.startsWith("windows") && !dumpUtil.contains(".exe")) {
            dumpUtil += ".exe";
        }

        // 判断该路径下是否已有备份工具
        File file = Paths.get(dumpUtil).toFile();
        if (!file.exists()) {
            // 获取备份工具数据流
            // TestTask.class 是当前类, 任意一个项目中的类也可
            InputStream inputStream = TestTask.class.getClassLoader().getResourceAsStream(dumpUtil);
            if (inputStream != null) {
                try {
                    File t = new File(dumpUtil);
                    // 如果文件目录不存在, 先创建目录
                    if (t.getParentFile() != null && !t.getParentFile().exists()) {
                        boolean mkdirs = t.getParentFile().mkdirs();
                        log.info("备份工具目录创建结果: {}", mkdirs);
                    }
                    // 将备份工具提取出来
                    Files.copy(inputStream, Paths.get(dumpUtil));
                } catch (IOException e) {
                    throw new RuntimeException("备份工具不存在!", e);
                }
            } else {
                log.info("备份工具不存在!!!");
            }
        }
    }
}
